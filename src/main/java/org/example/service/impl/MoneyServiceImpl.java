package org.example.service.impl;

import org.example.entity.LongHuBean;
import org.example.entity.LongHuCountBean;
import org.example.entity.NiuSanConnect;
import org.example.entity.Stock;
import org.example.mapper.NewFlowInvestorMapper;
import org.example.mapper.NiuSanConnectMapper;
import org.example.mapper.StockMapper;
import org.example.req.LongHuReq;
import org.example.req.StockReq;
import org.example.resp.NiuSanResp;
import org.example.service.MoneyService;
import org.example.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

@Service
public class MoneyServiceImpl implements MoneyService {

    @Autowired
    private NewFlowInvestorMapper newFlowInvestorMapper;

    @Autowired
    private NiuSanConnectMapper  niuSanConnectMapper;

    @Autowired
    private HttpUtils utils;

    @Autowired
    private StockMapper stockMapper;

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

    /**
     * type 1 夏重阳夫妇
     * @param type
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<NiuSanResp> selectConnectByNameAndTime(String type,String startTime, String endTime) {
        startTime=startTime.replaceAll("-","");
        endTime=endTime.replaceAll("-","");
        List<NiuSanResp> realResp=new ArrayList<>();
        List<NiuSanResp> niuSanResps=newFlowInvestorMapper.selectByTime(startTime,endTime);

        for (int index=0;index<niuSanResps.size();index++){
            NiuSanResp resp=niuSanResps.get(index);
            List<String> nameList=new ArrayList<>();
            if (!StringUtils.isEmpty(resp.allHolderName)){
                String[] names=resp.allHolderName.split(",");
                nameList.addAll(Arrays.asList(names));
            }
            resp.setHolderNameList(nameList);
        }

        if (type.equals("1")){
            Map<String,NiuSanResp> map=pickByXiaChongYang(niuSanResps);
            for (String key : map.keySet()) {
                realResp.add(map.get(key));
            }
        }else if (type.equals("2")){
            Map<String,NiuSanResp> map=pickByXuXiang(niuSanResps);
            for (String key : map.keySet()) {
                realResp.add(map.get(key));
            }
        }
        return realResp;
    }

    private Map<String,NiuSanResp>  pickByXiaChongYang(List<NiuSanResp> niuSanResps){
        Map<String,NiuSanResp> mapResource=new HashMap<>();
        Map<String,NiuSanResp> map2=new HashMap<>();
        List<NiuSanConnect> niuSanConnectList=niuSanConnectMapper.queryConnectByName("夏重阳");
        getPickSymbol(mapResource, niuSanConnectList, niuSanResps);

        List<NiuSanConnect> niuSanConnectList2=niuSanConnectMapper.queryConnectByName("张素芬");
        getPickSymbol(map2, niuSanConnectList2, niuSanResps);

        for (String key : map2.keySet()) {
            if (!mapResource.containsKey(key)){
                mapResource.put(key,map2.get(key));
            }
        }
        return mapResource;
    }




    private Map<String,NiuSanResp>  pickByXuXiang(List<NiuSanResp> niuSanResps){
        Map<String,NiuSanResp> mapResource=new HashMap<>();

        List<NiuSanConnect> niuSanConnectList=niuSanConnectMapper.queryConnectByName("徐翔");
        getPickSymbol(mapResource, niuSanConnectList, niuSanResps);

        Map<String,NiuSanResp> mapWxa=new HashMap<>();
        List<NiuSanConnect> niuSanConnectListWxa=niuSanConnectMapper.queryConnectByName("王孝安");
        getPickSymbol(mapWxa, niuSanConnectListWxa, niuSanResps);

        for (String key : mapWxa.keySet()) {
            if (!mapResource.containsKey(key)){
                mapResource.put(key,mapWxa.get(key));
            }
        }

        Map<String,NiuSanResp> mapGwd=new HashMap<>();
        List<NiuSanConnect> niuSanConnectListGwd=niuSanConnectMapper.queryConnectByName("葛卫东");
        getPickSymbol(mapGwd, niuSanConnectListGwd, niuSanResps);

        for (String key : mapGwd.keySet()) {
            if (!mapResource.containsKey(key)){
                mapResource.put(key,mapGwd.get(key));
            }
        }

        Map<String,NiuSanResp> mapMx=new HashMap<>();
        List<NiuSanConnect> niuSanConnectListMx=niuSanConnectMapper.queryConnectByName("马渲");
        getPickSymbol(mapMx, niuSanConnectListMx, niuSanResps);

        for (String key : mapMx.keySet()) {
            if (!mapResource.containsKey(key)){
                mapResource.put(key,mapMx.get(key));
            }
        }

        Map<String,NiuSanResp> mapTwb=new HashMap<>();
        List<NiuSanConnect> niuSanConnectListTwb=niuSanConnectMapper.queryConnectByName("屠文斌");
        getPickSymbol(mapTwb, niuSanConnectListTwb, niuSanResps);


        Map<String,NiuSanResp> mapZjp=new HashMap<>();
        List<NiuSanConnect> niuSanConnectListZjp=niuSanConnectMapper.queryConnectByName("章建平");
        getPickSymbol(mapZjp, niuSanConnectListZjp, niuSanResps);


        Map<String,NiuSanResp> mapSyq=new HashMap<>();
        List<NiuSanConnect> niuSanConnectListSyq=niuSanConnectMapper.queryConnectByName("施玉庆");
        getPickSymbol(mapSyq, niuSanConnectListSyq, niuSanResps);

        for (String key : mapTwb.keySet()) {
            if (!mapResource.containsKey(key)){
                mapResource.put(key,mapTwb.get(key));
            }
        }
        return mapResource;
    }


    private Map<String,NiuSanResp> getPickSymbol(Map<String,NiuSanResp> map ,List<NiuSanConnect> niuSanConnectList, List<NiuSanResp> niuSanResps){
        for (NiuSanResp niuSanResp : niuSanResps) {
            if (niuSanResp.getPickName().size()>=2)continue;
            niuSanResp.getPickName().clear();
            int pickCount=0;
            for (NiuSanConnect niuSanConnect : niuSanConnectList) {
                if (niuSanResp.getHolderNameList().contains(niuSanConnect.getSecondPeople())){
                    pickCount++;
                    if (pickCount>=2){
                        niuSanResp.getPickName().add(niuSanConnect.getSecondPeople());
                        map.put(niuSanResp.name,niuSanResp);
                        break;
                    }else {
                        niuSanResp.getPickName().add(niuSanConnect.getSecondPeople());
                    }
                }
            }
        }

        return map;
    }


    @Override
    public List<NiuSanConnect>  getNiuSanConnectByName(String type) {
        List<NiuSanConnect> niuSanConnectList=new ArrayList<>();;
        if (type.equals("1")){
            niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("夏重阳"));
            niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("张素芬"));
        }else if (type.equals("2")){
            niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("马渲"));
            niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("葛卫东"));
            niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("屠文斌"));
            niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("王孝安"));
            niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("徐翔"));
            niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("章建平"));
            niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("施玉庆"));
        }
        return niuSanConnectList;
    }

    public void insertNiuSanConnect(String name){
        List<NiuSanResp> resps= newFlowInvestorMapper.queryNewSanByName(name);

        Iterator<NiuSanResp> iterator = resps.iterator();   //剔除牛散模糊搜索出来的其他股票
        while (iterator.hasNext()) {
            NiuSanResp resp = iterator.next();
            String[] names=resp.getAllHolderName().split(",");
            boolean contains = Arrays.asList(names).contains(name);
            if (!contains){
                iterator.remove();
            }
        }


        //查询出该牛散做过的所有股票

        Set<String> symbols=new HashSet<>();
        for (NiuSanResp resp:resps){
            symbols.add(resp.getSymbol()+","+resp.getName());
        }

        Map<String,NiuSanConnect> maps=new HashMap<>();
        //记录每一只股票里出现过的所有牛散
        for (String symbol : symbols) {
            String symbolCode=symbol.split(",")[0];
            String symbolName=symbol.split(",")[1];
            List<NiuSanResp> respList=newFlowInvestorMapper.queryNewSanByCode(symbolCode);
            Set<String> symbolsWithName=new HashSet<>();   //获取每只股票里不重复的牛散
            for (NiuSanResp resp : respList) {
                String[] names=resp.getAllHolderName().split(",");
                for (String niusan : names) {
                    if (!(niusan.isEmpty()||niusan.equals(name))){
                        symbolsWithName.add(niusan);
                    }

                }
            }

            for (String s : symbolsWithName) {
                if (maps.containsKey(s)){
                    int count=Integer.parseInt(maps.get(s).getConnectCount());
                    count=count+1;
                    maps.get(s).setConnectCount(count+"");
                    String currentSymbolName= maps.get(s).getConnectSymbol();
                    maps.get(s).setConnectSymbol(currentSymbolName+","+symbolName);
                }else {
                    NiuSanConnect niuSanConnect=new NiuSanConnect();
                    niuSanConnect.setConnectCount("1");
                    niuSanConnect.setSecondPeople(s);
                    niuSanConnect.setFirstPeople(name);
                    niuSanConnect.setConnectSymbol(symbolName);
                    maps.put(s,niuSanConnect);
                }
            }

        }

        List<NiuSanConnect> niuSanConnects=new ArrayList<>();
        for (String niusan : maps.keySet()) {  //从map中取出所有相关数据，count小于2的剔除
            NiuSanConnect connect=maps.get(niusan);
            if (Integer.parseInt(connect.getConnectCount())>1){
                niuSanConnects.add(connect);
            }
        }
        niuSanConnectMapper.batchInsert(niuSanConnects);
        System.out.println(name+"----插入成功");
    }

    @Override
    public List<LongHuCountBean> getLongHuByTime(String time) throws Exception{
        StockReq req=new StockReq();
        req.setApi_name("top_inst");
        req.setToken("d8265273e779951ec1d0270da4d72b4519f66b808082df418bbf9cad");
        LongHuReq longHuReq=new LongHuReq();
        longHuReq.setTrade_date("20261016");
        req.setParams(longHuReq);

        String result=utils.postJson("http://api.tushare.pro",req);
        JSONObject jsonObject=new JSONObject(result);

        JSONObject data=jsonObject.getJSONObject("data");
        System.out.println(data);
        JSONArray jsonArray=data.getJSONArray("items");
        List<LongHuBean> list=new ArrayList<>();
        for (int index=0;index<jsonArray.length();index++){
            LongHuBean bean=new LongHuBean();
            JSONArray singleLonghu=jsonArray.getJSONArray(index);
            bean.setTrade_date(singleLonghu.optString(0));
            bean.setTs_code(singleLonghu.optString(1));
            bean.setExalter(singleLonghu.optString(2));
            bean.setBuy(singleLonghu.optString(3));
            bean.setBuy_rate(singleLonghu.optString(4));
            bean.setSell(singleLonghu.optString(5));
            bean.setSell_rate(singleLonghu.optString(6));
            bean.setNet_buy(singleLonghu.optString(7));
            bean.setSide(singleLonghu.optString(8));
            bean.setReason(singleLonghu.optString(9));
            list.add(bean);
        }
        return compareData(list);
    }

    private List<LongHuCountBean> compareData(List<LongHuBean> list){
        List<LongHuCountBean> longHuCountBeans=new ArrayList<>();
        String currentCode="";
        for (LongHuBean bean : list) {
            if (!Objects.equals(bean.getTs_code(), currentCode)){
                List<Stock> stocks=stockMapper.queryStockByTsCode(bean.getTs_code());
                LongHuCountBean longHuCountBean=new LongHuCountBean();
                if (!stocks.isEmpty()){
                    longHuCountBean.setStockCode(stocks.get(0).getSymbol());
                    longHuCountBean.setStockName(stocks.get(0).getName());
                }
                longHuCountBeans.add(longHuCountBean);
            }
//            if (bean.getSide().equals("0")){
//                longHuCountBeans.get(longHuCountBeans.size()-1).getStockLonghuBuy().add(bean);
//            }else {
//                longHuCountBeans.get(longHuCountBeans.size()-1).getStockLonghuSell().add(bean);
//            }
            longHuCountBeans.get(longHuCountBeans.size()-1).getStockLonghuBuy().add(bean);

            currentCode=bean.getTs_code();
        }
        return longHuCountBeans;
    }
}
