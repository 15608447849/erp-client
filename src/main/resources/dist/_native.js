/* eslint-disable */
(function () {

    if (window.JNB) return;

    //判断浏览器
    function isPcBrowser() {
         return "undefined" == typeof native
    }

    function isArray(params){
        let flag = false;
        if (params instanceof Array) {
            flag = true;
            params.forEach(element => {

                if(Object.prototype.toString.call(element) === '[object Object]'){
                    flag = false;
                }
            });
        }
        return flag;
    }

    function query({ serverName, cls, method, page = 1, count = 10 ,params = {}, callback}){

        if (isPcBrowser()){
            _reqNetwork(serverName, cls, method, page, count , params, callback);
        }else{
            try{
                _requestNative(_transfer(serverName,cls,method, page, count),convertString(params),callback)
            }catch(ex){
                if(isFunction(callback)){
                    callback(errorfun(ex))
                }
            }
        }
    }

    function _reqNetwork(server,clazz,method,page, count,params,callback){
      
        if (!isPcBrowser()) return

        let ic =  Ice.initialize(['--Ice.Default.Locator=DemoIceGrid/Locator:ws -h 114.116.155.221 -p 4062']);
        //let ic =  Ice.initialize(['--Ice.Default.Locator=DemoIceGrid/Locator:ws -h 192.168.1.35 -p 4062']);
        //let ic =  Ice.initialize(['--Ice.Default.Locator=DemoIceGrid/Locator:ws -h 114.116.149.145 -p 4062']);
        //let ic =  Ice.initialize(['--Ice.Default.Locator=DemoIceGrid/Locator:ws -h 47.106.100.121 -p 4062']);
        //let ic =  Ice.initialize(['--Ice.Default.Locator=DemoIceGrid/Locator:ws -h 192.168.1.119 -p 4062']);
        if (arguments.length < 4) {
            throw new Error('参数不正确');
        }

        if (arguments.length == 4) {
            callback = params
            params = null
        }
        
        if(!isFunction(callback)){
            throw new Error("callback is not function!")
        }

        let req = new inf.IRequest()
        req.cls = clazz
        req.method = method

        // let uuid = getCache("app-web-debug")
        let uuid = localStorage.getItem("app-web-debug")
        if (!uuid){
            uuid = Math.random() +"@APP_WEB_DEBUG";
            localStorage.setItem("app-web-debug",uuid)
        }
        req.param.token = uuid

        if (isNotNull(params)) {
            if(isArray(params)){
                req.param.arrays = params
            }else{
                req.param.json = convertString(params);
            }
        }


        req.param.pageIndex = page
        req.param.pageNumber = count

        Ice.Promise.try(
            function () {
                return inf.InterfacesPrx.checkedCast(ic.stringToProxy(server))
            }
        )
            .then(
                function (proxy) {
                    return proxy.accessService(req);
                }
            )
            .then(
                function(resp){

                    if (!_interceptorCallback(resp)) {
                        callback(resp);
                    }
                }
            )
            .finally(
                function()
                {
                    if(ic) {
                        return ic.destroy();
                    }
                }
            ).exception(
            function(ex) {
                console.error(ex);
                callback(errorfun(ex))
            });

    }

    function errorfun(ex){
        return JSON.stringify({code:-200,data:ex})
    }

    //判断是函数
    function isFunction(obj) {
        return typeof obj !== "undefined" && typeof obj === "function" && typeof obj.nodeType !== "number";
    }

    //判断非空
    function isNotNull(str) {
        return str && str !== 'null';
    }

    /*js注册,提供给native可以调用的函数*/
    const _js_register_functions = {};

    const _interceptorList = []

    //注册结果拦截器
    function _interceptor(function_imp) {
        _interceptorList.push(function_imp)
    }

    //拦截执行
    function _interceptorCallback(resp) {
        let intercep = false
        for(let _index in _interceptorList) {
            try{
                intercep = _interceptorList[_index](resp)

                if (intercep) {
                    break
                }
            }catch (e) {
                console.error("拦截:" + e +" , resp = "+ resp )
            }
        }
        return intercep
    }


    /*注册接口方法函数 */
    function _register(function_name, function_imp) {
        if (isFunction(function_imp)) {
            _js_register_functions[function_name] = function_imp;
        }
    }

    //对象转json字符串
    function convertString(obj) {
        if (obj && typeof obj === 'object') {
            return JSON.stringify(obj);
        }
        return obj;
    }

    //字符串转对象
    function convertObject(str) {
        var obj;
        try {
            obj = JSON.parse(str);
        } catch (e) {
            obj = str;
        }
        return obj;
    }

    //回调函数临时存储集合
    const _callback_fun_ids = {};

    //回调标识序号
    var uniqueId = 1;

    //转发
    function _transfer(serverName, cls, method,page = 1,count = 10) {
        if (isNotNull(serverName) && isNotNull(cls)){
            return "ts:" + serverName + "@" + cls + "@" + method +"@" + page +"@"+count;
        }
        return serverName;
    }

    /*向native发送请求信息, native的方法名 , 内容string , 回调函数 */
    function _requestNative(method, content, response_callback) {
        if (isPcBrowser()) return
        //判断当前参数
        if (arguments.length === 0) {
            throw new Error('参数不正确');
        }
        if (arguments.length === 1) {
            content = null;
            response_callback = null;
        }
        if (arguments.length === 2) {
            if (isFunction(content)) {
                response_callback = content;
                content = null;
            }
        }

        let callbackId = null;
        //如果存在回调函数,存储
        if (isFunction(response_callback)) {

            if (isFunction(response_callback)) {
                callbackId = 'js_callback_' + (uniqueId++) + '_' + new Date().getTime();
                _callback_fun_ids[callbackId] = response_callback;
            }
        }

        try {
            native.invoke(method, content, callbackId);
        } catch (exception) {
            if (isFunction(response_callback)) {
                //移除回调接口
                delete _callback_fun_ids[callbackId];
            }
            throw exception;
        }
    }

    /*js调用native后, native的回调*/
    function _callbackInvoke(callback_fun_id, result) {
        setTimeout(function () {
            let response_callback = _callback_fun_ids[callback_fun_id];
            if (!response_callback) {
                console.error(callback_fun_id + ' callback function doesn\'t exist!');
                return;
            }
            //移除回调接口
            delete _callback_fun_ids[callback_fun_id];
            //调用函数 传递结果字符串
            if (!_interceptorCallback(result)) {
                response_callback(result);
            }
        });
    }

    /*native对js的主动调用传递消息, function_name js端的函数名, 数据文本,native端的回调函数id*/
    function _invoke(function_name, content, callback_id) {
        setTimeout(function () {
            let value = null;
            let function_imp = _js_register_functions[function_name];
            if (function_imp) {
                try {
                    //执行函数
                    value = function_imp(content); //返回 string类型
                } catch (exception) {
                    console.error(exception);
                    value = "except:" + exception;
                }
                if (typeof value === "undefined") {
                    value = null;
                } else if (typeof value === "object") {
                    value = JSON.stringify(value);
                }
            } else {
                //没有找到函数
                value = "except:" + function_name + " doesn\'t exist!";
            }
            if (isNotNull(callback_id)) {
                try {
                    //回调给native结果
                    native.callbackInvoke(callback_id, value);
                } catch (exception) {
                    //打印错误信息
                    console.error(exception);
                }
            }

        });
    }

    let JNB = window.JNB = {
        _register: _register, //注册
        _interceptor:_interceptor,
        _transfer: _transfer, //生成转发
        _requestNative: _requestNative, //请求native方法
        _callbackInvoke: _callbackInvoke, //native 回调
        _invoke: _invoke ,//native调用js方法
        _reqNetwork:_reqNetwork, //websocket 网络调用
        query:query //总控制
    };

    let doc = document;
    let readyEvent = doc.createEvent('Events');
    readyEvent.initEvent('JNB_Init');
    readyEvent.bridge = JNB;
    doc.dispatchEvent(readyEvent);
    console.log("JNB ok")
})();

