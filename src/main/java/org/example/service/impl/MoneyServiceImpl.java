package org.example.service.impl;

import org.example.entity.NewFlowInvertor;
import org.example.mapper.NewFlowInvestorMapper;
import org.example.resp.NiuSanResp;
import org.example.service.MoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class MoneyServiceImpl implements MoneyService {

    @Autowired
    private NewFlowInvestorMapper newFlowInvestorMapper;

    @Override
    public List<NiuSanResp> queryNiuSanByName(String name) {
        List<NiuSanResp> resps= newFlowInvestorMapper.queryNewSanByName(name);

        Iterator<NiuSanResp> iterator = resps.iterator();
        while (iterator.hasNext()) {
            NiuSanResp resp = iterator.next();
            String[] names=resp.getAllHolderName().split(",");
            boolean contains = Arrays.asList(names).contains(name);
            if (!contains){
                iterator.remove();
            }
        }

        for (int index=0;index<resps.size();index++){
            NiuSanResp resp=resps.get(index);
            List<String> nameList=new ArrayList<>();
            if (!StringUtils.isEmpty(resp.allHolderName)){
                String[] names=resp.allHolderName.split(",");
                nameList.addAll(Arrays.asList(names));
            }
            resp.setHolderNameList(nameList);
        }
        return resps;

    }

    @Override
    public List<NiuSanResp> getNiuSanByNameV2(String name) {
        List<NiuSanResp> resps= newFlowInvestorMapper.queryNewSanByName(name);

        Iterator<NiuSanResp> iterator = resps.iterator();
        while (iterator.hasNext()) {
            NiuSanResp resp = iterator.next();
            String[] names=resp.getAllHolderName().split(",");
            boolean contains = Arrays.asList(names).contains(name);
            if (!contains){
                iterator.remove();
            }
        }

        for (int index=0;index<resps.size();index++){
            NiuSanResp resp=resps.get(index);
            List<String> nameList=new ArrayList<>();
            if (!StringUtils.isEmpty(resp.allHolderName)){
                String[] names=resp.allHolderName.split(",");
                nameList.addAll(Arrays.asList(names));
            }
            resp.setHolderNameList(nameList);
        }
        resps=splitChildList(resps);
        resps.sort(Comparator.comparing(NiuSanResp::getEndDate).reversed());
        return resps;
    }


    public List<NiuSanResp> splitChildList(List<NiuSanResp> niuSanRespList){
        List<NiuSanResp> resps=new ArrayList<>();
        String currentSymbol="";
        for (int index=0;index<niuSanRespList.size();index++){
            NiuSanResp ns=niuSanRespList.get(index);
            if (!ns.symbol.equals(currentSymbol)){
                currentSymbol=ns.symbol;
                NiuSanResp niuSanResp=new NiuSanResp();
                niuSanResp.symbol=ns.symbol;
                niuSanResp.name=ns.name;
                niuSanResp.endDate=ns.endDate;
                niuSanResp.setHolderNameList(ns.holderNameList);
                resps.add(niuSanResp);
            }else {
                resps.get(resps.size()-1).childNiuSanList.add(ns);
            }

        }
        return resps;
    }

    @Override
    public List<NiuSanResp> queryNiuSanByCode(String code) {
        List<NiuSanResp> resps= newFlowInvestorMapper.queryNewSanByCode(code);

        for (int index=0;index<resps.size();index++){
            NiuSanResp resp=resps.get(index);
            List<String> nameList=new ArrayList<>();
            if (!StringUtils.isEmpty(resp.allHolderName)){
                String[] names=resp.allHolderName.split(",");
                nameList.addAll(Arrays.asList(names));
            }
            resp.setHolderNameList(nameList);
        }
        return resps;
    }
}
