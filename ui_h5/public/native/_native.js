/* eslint-disable */
(function () {

    if (window.JNB) return;

    //判断是函数
    function isFunction(obj) {
        return typeof obj !== "undefined" && typeof obj === "function" && typeof obj.nodeType !== "number";
    }

    //判断非空
    function isNotNull(str) {
        return str && str !== 'null';
    }

    /* 对象转json字符串 */
     function convertString(obj) {
          if (obj && typeof obj === 'object') {
              return JSON.stringify(obj);
          }
          return obj;
      }

    //判断浏览器
    function isPcBrowser() {
         return "undefined" == typeof jsBridge
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

    function request({ serverName, cls, method, page = 1, count = 10 ,params = {}, callback}){
        if (isPcBrowser()){
            _requestIceNetwork(serverName, cls, method, page, count , params, callback);
        }else{

            try{
                jsBridge._requestNative(jsBridge._transfer(serverName,cls,method, page, count),convertString(params),callback)
            }catch(ex){
                console.log(ex)
                if(isFunction(callback)){
                    callback(errorfun(ex))
                }
            }
        }
    }

    function _requestIceNetwork(server,clazz,method,page, count,params,callback){

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

                    if(isPcBrowser()){
                        callback(resp);
                    }else if (!_interceptorCallback(resp)) {
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

   /* 注册接口方法函数 */
    function _register(function_name, function_imp) {
       if(isPcBrowser()) return;
       jsBridge._register(function_name,function_imp);
    }

  //注册结果拦截器
    function _interceptor(function_imp) {
        if(isPcBrowser()) return;
        jsBridge._interceptor(function_imp)
    }

    let JNB = window.JNB = {
        _register: _register, //注册
        _interceptor:_interceptor,
        request:request //
    };

    let doc = document;
    let readyEvent = doc.createEvent('Events');
    readyEvent.initEvent('JNB_Init');
    readyEvent.bridge = JNB;
    doc.dispatchEvent(readyEvent);
    console.log("JNB ok")
})();

