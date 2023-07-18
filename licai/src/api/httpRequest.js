import axios from "axios";
import qs from 'qs';
import layx from "vue-layx";

//设置默认值
axios.defaults.baseURL = "http://localhost:8000/api";
axios.defaults.timeout = 50000

//get请求
function doGet(url, params) {
    return axios({
        url: url,
        method: 'get',
        params: params
    });
}

function doPostJson(url, params) {
    return axios({
        url: url,
        method: 'post',
        data: params
    })
}

function doPost(url, params) {
    let reguestData = qs.stringify(params);
    return axios.post(url, reguestData);
}

//创建拦截器
axios.interceptors.request.use(function (config) {
    //在需要用户登录后的操作，在请求的url中加入token
    //判断访问服务器的url地址，需要提供身份信息，加入token
    let storageToken = window.localStorage.getItem("token");
    let userinfo = window.localStorage.getItem("userinfo");
    if (storageToken && userinfo) {
        if (config.url == '/v1/user/realname' || config.url == '/v1/user/usercenter' ||
            config.url == '/v1/recharge/records' || config.url == '/v1/invest/product' ||
            config.url == '/v1/invest/records' || config.url == '/v1/income/records') {
            //在header中传递token和一个userId
            config.headers['Authorization'] = 'bearer ' + storageToken;
            config.headers['uid'] = JSON.parse(userinfo).uid;
        }
    }
    return config;
}, function (err) {
    console.log("请求错误" + err)
})
//创建应答拦截器，统一对错误处理，后端返回code > 1000 都是错误
axios.interceptors.response.use(function (resp) {
    if (resp && resp.data.code > 1000) {
        let code = resp.data.code;
        if (code == 3000) {
            //token无效，重新登录
            window.location.href = '/page/user/login';
        } else {
            layx.msg(resp.data.msg, {dialogIcon: 'warn', position: 'ct'})
        }
    }
    return resp;
}, function (err) {
    console.log("应答拦截器错误=" + err)
    //回到首页
    //window.location.href = '/';
})
//导出，暴露这个函数
export {doGet, doPostJson, doPost}