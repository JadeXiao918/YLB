package com.ylb.front.controller;

import com.ylb.api.model.IncomeRecord;
import com.ylb.common.util.CommonUtil;
import com.ylb.front.view.RespResult;
import com.ylb.front.view.income.IncomeResultView;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "收益业务")
@RestController
public class IncomeController extends BaseController{
    //查询收益流水
    @ApiOperation(value = "查询某个用户的收益记录")
    @GetMapping("/v1/income/records")
    public RespResult queryIncomePage(@RequestHeader("uid") Integer uid,
                                      @RequestParam(required = false,defaultValue = "1") Integer pageNo,
                                      @RequestParam(required = false,defaultValue = "6") Integer pageSize){
        RespResult result = RespResult.fail();
        if (uid != null && uid > 1){
            List<IncomeRecord> records = incomeService.queryByUid(uid,pageNo,pageSize);
            result = RespResult.ok();
            result.setList(toView(records));
        }
        return result;
    }
    private List<IncomeResultView> toView(List<IncomeRecord> src){
        List<IncomeResultView> target = new ArrayList<>();
        src.forEach(record -> {
            target.add(new IncomeResultView(record));
        });
        return target;
    }

}
