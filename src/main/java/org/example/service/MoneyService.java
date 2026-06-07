package org.example.service;

import org.example.entity.NewFlowInvertor;
import org.example.entity.NiuSanConnect;
import org.example.resp.NiuSanResp;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface MoneyService {

    List<NiuSanResp> queryNiuSanByName(String name);

    List<NiuSanResp> getNiuSanByNameV2(String name);

    List<NiuSanResp> queryNiuSanByCode(String code);

    List<NiuSanConnect> getNiuSanConnectByName(String name);

}
