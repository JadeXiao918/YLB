<template>
  <div>
    <Header></Header>
    <div class="content clearfix">
      <!--个人信息-->
      <div class="user-head">
        <div class="user-head-left fl">
          <div class="user-head-img">
            <img src="@/assets/image/user-head.png" alt="">
          </div>
          <p>上传头像</p>
        </div>
        <div class="user-head-right fl">
          <ul class="user-head-name fl">
            <li><b>{{ userBaseInfo.name }}</b></li>
            <li>{{ userBaseInfo.phone }}</li>
            <li>最近登录：{{ userBaseInfo.loginTime }}</li>
          </ul>
          <div class="user-head-money fr">
            <p>可用余额：<span>{{ userBaseInfo.money }}</span></p>
            <a href="javascript:void(0)" @click="goLink('/page/user/userpay')" style="color: red" class="user-head-a1">充值</a>
            <a href="javascript:void(0)" @click="goLink('/')" style="color: red" class="user-head-a2">投资</a>
          </div>
        </div>

      </div>
      <!--记录-->
      <div class="user-record-box clearfix">
        <div class="user-record user-record-1" v-if="isInvested">
          <h3 class="user-record-title">最近投资</h3>
          <table align="center" width="388" border="0" cellspacing="0" cellpadding="0">
            <thead class="datail_thead">
            <tr>
              <th>序号</th>
              <th>投资结果</th>
              <th>投资金额</th>
              <th>投资时间</th>
            </tr>
            </thead>
            <tbody >
            <tr v-for="(item,index) in bidList" :key="item.id">
              <td>{{index + 1}}</td>
              <td>{{item.result}}</td>
              <td>{{item.bidMoney}}</td>
              <td>{{item.bidDate}}</td>
            </tr>
            </tbody>
          </table>
        </div>
        <div class="user-record user-record-1" v-else>
          <h3 class="user-record-title">最近投资</h3>
          <table align="center" width="388" border="0" cellspacing="0" cellpadding="0">
            <thead class="datail_thead">
            <tr>
              <th>序号</th>
              <th>投资结果</th>
              <th>投资金额</th>
              <th>投资时间</th>
            </tr>
            </thead>
          </table>
          <!--无记录-->
          <p class="user-record-no">还没有投资记录，请投资：<a href="javascript:void(0)" @click="goLink('/page/product/list')">投资</a></p>
        </div>
        <div class="user-record user-record-2" v-if="isRecharged">
          <h3 class="user-record-title">最近充值</h3>
          <table align="center" width="388" border="0" cellspacing="0" cellpadding="0">
            <thead class="datail_thead">
            <tr>
              <th>序号</th>
              <th>充值结果</th>
              <th>充值金额</th>
              <th>充值时间</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(item,index) in rechargeList" :key="item.id">
              <td>{{ index + 1 }}</td>
              <td>{{ item.result }}</td>
              <td>{{ item.rechargeMoney }}</td>
              <td>{{ item.rechargeDate }}</td>
            </tr>
            </tbody>
          </table>
        </div>
        <div class="user-record user-record-2" v-else>
          <h3 class="user-record-title">最近充值</h3>
          <table align="center" width="388" border="0" cellspacing="0" cellpadding="0">
            <thead class="datail_thead">
            <tr>
              <th>序号</th>
              <th>充值结果</th>
              <th>充值金额</th>
              <th>充值时间</th>
            </tr>
            </thead>
          </table>
          <!--无记录-->
          <p class="user-record-no">还没有充值记录，请充值：<a href="javascript:void(0)"
                                                   @click="goLink('/page/user/userpay')">充值</a></p>
        </div>
        <div class="user-record user-record-3" v-if="isIncomed">
          <h3 class="user-record-title ">最近收益</h3>
          <table align="center" width="388" border="0" cellspacing="0" cellpadding="0">
            <thead class="datail_thead">
            <tr>
              <th>序号</th>
              <th>收益状态</th>
              <th>收益金额</th>
              <th>投资日期</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(item,index) in incomeList" :key="item.id">
              <td>{{index + 1}}</td>
              <td>{{item.result}}</td>
              <td>{{item.incomeMoney}}</td>
              <td>{{item.incomeDate}}</td>
            </tr>
            </tbody>
          </table>
        </div>
        <div class="user-record user-record-3" v-else>
          <h3 class="user-record-title ">最近收益</h3>
          <table align="center" width="388" border="0" cellspacing="0" cellpadding="0">
            <thead class="datail_thead">
            <tr>
              <th>序号</th>
              <th>收益状态</th>
              <th>收益金额</th>
              <th>投资日期</th>
            </tr>
            </thead>
          </table>
          <!--无记录-->
          <p class="user-record-no">还没有收益记录</p>
        </div>

      </div>


    </div>
    <Footer></Footer>
  </div>
</template>

<script>
import Header from "@/components/common/Header";
import Footer from "@/components/common/Footer";
import {doGet} from "@/api/httpRequest";
import router from "@/router";

export default {
  name: "UserCenterView",
  components: {
    // eslint-disable-next-line vue/no-unused-components
    Header,
    // eslint-disable-next-line vue/no-unused-components
    Footer
  },
  data() {
    return {
      userBaseInfo: {
        loginTime: "",
        money: 0.0,
        phone: "",
        name: "",
        headerUrl: ""
      },
      rechargeList: {
        id: 0,
        result: "",
        rechargeDate: "",
        rechargeMoney: 0
      },
      bidList: {
        id: 0,
        result: "",
        bidDate: "",
        bidMoney: 0.0
      },
      isInvested: false,
      incomeList:{
        id: 0,
        result: "",
        incomeDate: "",
        incomeMoney: 0
      },
      isIncomed: false,
      isRecharged: false
    }
  },
  mounted() {
    doGet('/v1/user/usercenter').then(resp => {
      if (resp && resp.data.code == 1000) {
        this.userBaseInfo = resp.data.data;
      }
    });
    doGet('/v1/recharge/records', {pageNo: 1, pageSize: 6}).then(resp => {
      if (resp && resp.data.code == 1000) {
        this.rechargeList = resp.data.list;
        if (this.rechargeList != null){
          this.isRecharged = true;
        }
      }
    });
    doGet('/v1/invest/records',{pageNo: 1,pageSize: 6}).then(resp=>{
      if (resp && resp.data.code == 1000){
        this.bidList = resp.data.list;
        if (this.bidList != null){
          this.isInvested = true;
        }
      }
    });
    doGet('/v1/income/records',{pageNo: 1,pageSize: 6}).then(resp=>{
      if (resp && resp.data.code == 1000){
        this.incomeList = resp.data.list;
        if (this.incomeList != null){
          this.isIncomed = true;
        }
      }
    })
  },
  methods: {
    goLink(url, params) {
      //使用router做页面跳转，vue中的对象
      router.push({
        path: url,
        query: params
      })
    },
  }
}
</script>

<style scoped>

</style>