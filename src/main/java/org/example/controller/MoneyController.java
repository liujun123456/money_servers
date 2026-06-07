package org.example.controller;

import org.example.dto.ResponseDTO;
import org.example.entity.NiuSanConnect;
import org.example.resp.NiuSanResp;
import org.example.service.MoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/money")
@Validated
public class MoneyController {

    @Autowired
    private MoneyService moneyService;

//    @GetMapping("/{id}")
//    public ResponseDTO<User> getUserById(@PathVariable Long id) {
//        User user = userService.getUserById(id);
//        return ResponseDTO.success(user);
//    }

    @GetMapping("niuSan/{name}")
    public ResponseDTO<List<NiuSanResp>> getNiuSanByName(@PathVariable String name){

        return ResponseDTO.success(moneyService.queryNiuSanByName(name));
    }

    @GetMapping("niuSanv2/{name}")
    public ResponseDTO<List<NiuSanResp>> getNiuSanByNameV2(@PathVariable String name){

        return ResponseDTO.success(moneyService.getNiuSanByNameV2(name));
    }


    @GetMapping("symbol/{code}")
    public ResponseDTO<List<NiuSanResp>> getNiuSanBySymbol(@PathVariable String code){
        return ResponseDTO.success(moneyService.queryNiuSanByCode(code));
    }

    @GetMapping("niusan/connect/{type}/{startTime}/{endTime}")
    public ResponseDTO<List<NiuSanResp>> getSymbolByTime(@PathVariable String type,@PathVariable String startTime,@PathVariable String endTime){
        return ResponseDTO.success(moneyService.selectConnectByNameAndTime(type,startTime,endTime));
    }

    @GetMapping("niuSan/connection/{name}")
    public ResponseDTO<List<NiuSanConnect>> getNiuSanConnectByName(@PathVariable String name){
        return ResponseDTO.success(moneyService.getNiuSanConnectByName(name));
    }



}
