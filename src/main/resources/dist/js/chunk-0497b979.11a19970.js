(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-0497b979"],{"07cb":function(t,e,n){"use strict";n.r(e);var o=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"home"},[n("van-search",{attrs:{placeholder:"通过药店名/药店手机号码搜索","show-action":"",shape:"round"},on:{search:t.onSearch,clear:t.kill,blur:t.blur},model:{value:t.keyword,callback:function(e){t.keyword=e},expression:"keyword"}},[n("div",{attrs:{slot:"action"},on:{click:t.onSearch},slot:"action"},[t._v("搜索")])]),n("van-cell-group",[n("van-field",{staticClass:"text1",attrs:{value:"手机号码",label:"药店名称","left-icon":"friends-o",readonly:"","label-width":"120","input-align":"center"}},[n("van-button",{staticClass:"btn",attrs:{slot:"button",size:"small",type:"primary",disabled:""},slot:"button"},[t._v("审核状态")])],1),n("van-list",{attrs:{finished:t.finished,"finished-text":"没有更多了","immediate-check":!1,offset:30},on:{load:t.onLoad},model:{value:t.loading,callback:function(e){t.loading=e},expression:"loading"}},t._l(t.list,function(e,o){return n("div",{key:o,on:{click:function(e){return t.toDetail(o)}}},[n("van-field",{staticClass:"field",attrs:{value:e.phone,label:e.company,"left-icon":"contact","label-width":"120",readonly:"",clickable:"","input-align":"center"}},[n("van-button",{attrs:{slot:"button",size:"small",type:"primary",disabled:""},slot:"button"},[t._v(t._s(t.formatState(e.status)))])],1)],1)}),0)],1),n("button",{ref:"button",class:"button "+t.hoverBtn,attrs:{type:"danger",block:""},on:{click:function(e){return e.preventDefault(),t.back(e)},touchmove:function(e){return e.preventDefault(),t.touchmove(e)},touchend:t.touchend}},[t._v("\n    退出\n  ")])],1)},s=[],i=(n("7f7f"),n("b970")),a={data:function(){return{keyword:"",list:[],zt:"",Info:{phone:"",name:"",address:"",status:"",bd:"",total:0,pageNo:1,pageSize:10},storeInfo:{},loading:!1,finished:!1,hoverBtn:"",mask:!0}},mounted:function(){this.storeInfo=this.$getBDUser(),this.getInfo()},methods:{kill:function(){this.Info.phone="",this.Info.name=""},formatState:function(t){var e="";return(128&t)>0?e="待审核":(256&t)>0?e="审核通过":(512&t)>0&&(e="不通过"),e},getInfo:function(){var t=this;this.mask&&(this.mask=!1,JNB.query({serverName:"userServer",cls:"BackGroundProxyMoudule",method:"queryStores",params:{cname:this.Info.name,uphone:this.Info.phone,ckstatus:""===this.Info.status?this.Info.status=-1:this.Info.status,address:this.Info.address,puid:this.storeInfo.userId,mroleid:this.storeInfo.roleCode,bd:""===this.Info.bd?this.Info.bd=0:this.Info.bd,pageNo:this.Info.pageNo,pageSize:this.Info.pageSize,createdate:""},callback:function(e){var n=JSON.parse(e);200==n.code&&(t.list=t.list.concat(n.data),t.Info.pageNo++,t.Info.pageNo>Math.ceil(n.total/n.pageSize)&&(t.finished=!0,t.Info.pageNo=1),t.loading=!1,t.mask=!0)}}))},onSearch:function(){this.Info.phone="",this.Info.name="",11===this.keyword.length?this.Info.phone=this.keyword:this.Info.name=this.keyword,this.list=[],this.Info.pageNo=1,this.finished=!1,this.getInfo()},toDetail:function(t){this.$router.push({path:"/bdDetail"}),sessionStorage.setItem("storeInfo",JSON.stringify(this.list[t]))},back:function(){var t=this;i["a"].confirm({title:"温馨提示",message:"确认要退出吗？"}).then(function(){JNB.query({serverName:"userServer",cls:"LoginRegistrationModule",method:"logout",callback:function(e){var n=JSON.parse(e);200==n.code&&(t.$delBDUser(),t.$toPage("home"))}})}).catch(function(){})},blur:function(){""==this.keyword&&(this.Info.phone="",this.Info.name="",this.list=[],this.getInfo())},onLoad:function(){this.getInfo()},touchmove:function(t){this.hoverBtn="hoverBtn";var e=document.documentElement.clientWidth,n=document.documentElement.clientHeight;this.$refs.button.style.position="fixed",t.changedTouches[0].clientX-25<=0?this.$refs.button.style.left=0:t.changedTouches[0].clientX-25>=e-50?this.$refs.button.style.left=e-50+"px":this.$refs.button.style.left=t.changedTouches[0].clientX-25+"px",t.changedTouches[0].clientY-100<=0?this.$refs.button.style.top="55px":t.changedTouches[0].clientY-25>=n-50?this.$refs.button.style.top=n-50+"px":this.$refs.button.style.top=t.changedTouches[0].clientY-25+"px"},touchend:function(){this.hoverBtn=""}}},c=a,l=(n("814c"),n("2877")),r=Object(l["a"])(c,o,s,!1,null,"1f3f9b2a",null);e["default"]=r.exports},"814c":function(t,e,n){"use strict";var o=n("d4c1"),s=n.n(o);s.a},d4c1:function(t,e,n){}}]);
//# sourceMappingURL=chunk-0497b979.11a19970.js.map