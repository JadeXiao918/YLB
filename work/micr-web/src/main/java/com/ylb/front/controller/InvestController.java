package com.ylb.front.controller;

import com.ylb.api.model.BidInfo;
import com.ylb.api.model.RechargeRecord;
import com.ylb.api.model.User;
import com.ylb.common.constants.RedisKey;
import com.ylb.common.util.CommonUtil;
import com.ylb.front.view.RespResult;
import com.ylb.front.view.bid.BidResultView;
import com.ylb.front.view.invest.RankView;
import com.ylb.front.view.recharge.ResultView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Api(tags = "投资理财产品")
//有关投资功能
@RestController
public class InvestController extends BaseController{
    //投资排行榜
    @ApiOperation(value = "投资排行榜",notes = "显示投资金额最高的3位用户信息")
    @GetMapping("/v1/invest/rank")
    public RespResult showInvestRank(){
        //从redis查询数据
        Set<ZSetOperations.TypedTuple<String>> sets =
                stringRedisTemplate.boundZSetOps(RedisKey.KEY_INVEST_RANK)
                .reverseRangeWithScores(0, 2);
        List<RankView> rankList = new ArrayList<>();
        //遍历set集合
        sets.forEach(tuple -> {
            //tuple.getValue();//手机号
            //tuple.getScore();//投资金额
            rankList.add(new RankView(CommonUtil.tuoMinPhone(tuple.getValue()),tuple.getScore()));
        });
        RespResult result = RespResult.ok();
        result.setList(rankList);
        return result;
    }
    //购买理财产品，更新投资排行榜
    @ApiOperation(value = "投资理财产品")
    @PostMapping("/v1/invest/product")
    public RespResult investProduct(@RequestParam("productId") Integer productId,
                                    @RequestParam("money")BigDecimal money,
                                    @RequestHeader("uid") Integer uid){
        RespResult result = RespResult.fail();
        //检查基本参数
        if ((uid != null && uid > 0) && (productId != null && productId > 0) && (money != null && money.intValue() % 100 == 0 && money.intValue() >= 100)){
            int investResult = inverstService.investProduct(uid,productId,money);
            switch (investResult){
                case 0:
                    result.setMsg("投资数据不正确");
                    break;
                case 1:
                    result = RespResult.ok();
                    modifyInvestRank(uid,money);
                    break;
                case 2:
                    result.setMsg("资金账号不存在");
                    break;
                case 3:
                    result.setMsg("资金不足");
                    break;
                case 4:
                    result.setMsg("产品不存在");
                    break;
            }
        }
        return result;
    }
    //更新投资排行榜
    private void modifyInvestRank(Integer uid,BigDecimal money){
        User user = userService.queryById(uid);
        if (user != null){
            //更新redis中的投资排行榜
            String key = RedisKey.KEY_INVEST_RANK;
            stringRedisTemplate.boundZSetOps(key).incrementScore(user.getPhone(),money.doubleValue());
        }
    }
    @ApiOperation(value = "查询某个用户的投资记录")
    @GetMapping("/v1/invest/records")
    public RespResult queryInvestPage(@RequestHeader("uid") Integer uid,
                                        @RequestParam(required = false,defaultValue = "1") Integer pageNo,
                                        @RequestParam(required = false,defaultValue = "6") Integer pageSize){
        RespResult result = RespResult.fail();
        if (uid != null && uid > 0){
            List<BidInfo> records = inverstService.queryByUid(uid, pageNo, pageSize);
            result = RespResult.ok();
            result.setList(toViewBid(records));
        }
        return result;
    }
    private List<BidResultView> toViewBid(List<BidInfo> src){
        List<BidResultView> target = new ArrayList<>();
        src.forEach(record -> {
            target.add(new BidResultView(record));
        });
        return target;
    }
}
