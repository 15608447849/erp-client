/* eslint-disable */
(function (){
    if (window.native) return;
    const PROTOCOL_HEADER = "bridge:"

    function getFnName(callee){
    var _callee = callee.toString().replace(/[\s\?]*/g,""),
          comb = _callee.length >= 50 ? 50 :_callee.length;
          _callee = _callee.substring(0,comb);
          var name = _callee.match(/^function([^\(]+?)\(/);
          if(name && name[1]){
            return name[1];
          }
          var caller = callee.caller,
          _caller = caller.toString().replace(/[\s\?]*/g,"");
          var last = _caller.indexOf(_callee),
          str = _caller.substring(last-30,last);
          name = str.match(/var([^\=]+?)\=/);
          if(name && name[1]){
            return name[1];
          }
          return "anonymous"
    }

    function callNative({mark=null, method , param , callbackId}){
        if  (mark) {
            alert(PROTOCOL_HEADER+JSON.stringify(arguments[0]));
        }
    }

    function invoke(method, param, callbackId){
        callNative({mark:getFnName(arguments.callee),method:method,param:param,callbackId:callbackId})
     }

     function callbackInvoke(callbackId, param){
        callNative({mark:getFnName(arguments.callee),param:param,callbackId:callbackId})
    }

    // function getData()

    let native = window.native = {
       invoke: invoke,
       callbackInvoke:callbackInvoke
    };
    
    let doc = document;
    let readyEvent = doc.createEvent('Events');
    readyEvent.initEvent('native_Init');
    readyEvent.bridge = native;
    doc.dispatchEvent(readyEvent);
    console.log("native ok");
})();

