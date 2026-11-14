package org.example.service.impl;

import org.example.entity.*;
import org.example.mapper.LonghuMapper;
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

    @Autowired
    private LonghuMapper longhuMapper;

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
    public List<LongHuBean> selectLhByName(String name) {
        List<LongHuBean> result=longhuMapper.selectByLh(name);
        return result;
    }

    @Override
    public List<LongHuCountBean> getLongHuByTime(String time) throws Exception{
        time=time.replaceAll("-","");
        StockReq req=new StockReq();
        req.setApi_name("top_inst");
        req.setToken("d8265273e779951ec1d0270da4d72b4519f66b808082df418bbf9cad");
        LongHuReq longHuReq=new LongHuReq();
        longHuReq.setTrade_date(time);
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
            bean.setTradeDate(singleLonghu.optString(0));
            bean.setTsCode(singleLonghu.optString(1));
            bean.setExalter(singleLonghu.optString(2));
            bean.setBuy(singleLonghu.optString(3));
            bean.setBuyRate(singleLonghu.optString(4));
            bean.setSell(singleLonghu.optString(5));
            bean.setSellRate(singleLonghu.optString(6));
            bean.setNetBuy(singleLonghu.optString(7));
            bean.setSide(singleLonghu.optString(8));
            bean.setReason(singleLonghu.optString(9));
            list.add(bean);
        }
        return compareData(list);
    }

    private List<LongHuCountBean> compareData(List<LongHuBean> list){
        List<LongHuCountBean> longHuCountBeans=new ArrayList<>();
        List<LongHuBean> localLongHu=buildLocalData(list);   //获取匹配上本地标记的营业部  所有龙虎榜数据
        Map<String,Integer> indexMap=new HashMap<>();
        for (int index=0;index<localLongHu.size();index++){
            LongHuBean bean=localLongHu.get(index);
            if (indexMap.containsKey(bean.getTsCode())){
                int mapIndex=indexMap.get(bean.getTsCode());
                if (bean.getSide().equals("0")){
                    if (!longHuCountBeans.get(mapIndex).getStockLonghuBuy().contains(bean.getExalter())&&!bean.getExalter().equals("机构专用")){
                        longHuCountBeans.get(mapIndex).getStockLonghuBuy().add(bean.getExalter());
                    }

                    if (bean.getExalter().equals("机构专用")){
                        longHuCountBeans.get(mapIndex).getStockLonghuBuy().add(bean.getExalter());
                    }

                }else {
                    if (!longHuCountBeans.get(mapIndex).getStockLonghuSell().contains(bean.getExalter())&&!bean.getExalter().equals("机构专用")){
                        longHuCountBeans.get(mapIndex).getStockLonghuSell().add(bean.getExalter());
                    }

                    if (bean.getExalter().equals("机构专用")){
                        longHuCountBeans.get(mapIndex).getStockLonghuSell().add(bean.getExalter());
                    }
                }
            }else {
                List<Stock> stocks=stockMapper.queryStockByTsCode(bean.getTsCode());
                if (!stocks.isEmpty()){
                    LongHuCountBean longHuCountBean=new LongHuCountBean();
                    longHuCountBean.setStockCode(stocks.get(0).getSymbol());
                    longHuCountBean.setStockName(stocks.get(0).getName());
                    if (bean.getSide().equals("0")){
                        longHuCountBean.getStockLonghuBuy().add(bean.getExalter());
                    }else {
                        longHuCountBean.getStockLonghuSell().add(bean.getExalter());
                    }
                    longHuCountBeans.add(longHuCountBean);
                    indexMap.put(bean.getTsCode(),longHuCountBeans.size()-1);
                }
            }
        }
        System.out.print(longHuCountBeans);
        return longHuCountBeans;
    }


    private List<LongHuBean>  buildLocalData(List<LongHuBean> list){
        List<LongHuBean> longHuWithCodes=new ArrayList<>();
        List<String> localLh=getLocalLongHuList();
        for (LongHuBean bean : list) {
            if (localLh.contains(bean.getExalter())){
                longHuWithCodes.add(bean);
            }
        }
        return longHuWithCodes;
    }

    private List<String>  getLocalLongHuList(){
        String localLh="国泰海通证券股份有限公司，中信证券股份有限公司上海分公司，国信证券股份有限公司浙江互联网分公司，华宝证券股份有限公司上海东大名路证券营业部，国泰海通证券股份有限公司武汉紫阳东路证券营业部，" +
                "国泰海通证券股份有限公司北京知春路证券营业部，国泰海通证券股份有限公司上海长宁区江苏路证券营业部，国泰海通证券股份有限公司南京太平南路证券营业部，国泰海通证券股份有限公司成都天府大道证券营业部，" +
                "国泰海通证券股份有限公司上海分公司，国泰海通证券股份有限公司成都双庆路证券营业部，国新证券股份有限公司北京分公司，国泰海通证券股份有限公司上海松江区中山东路证券营业部，" +
                "东方证券股份有限公司上海松江区沪亭北路证券营业部，国泰海通证券股份有限公司湛江万豪世家证券营业部，中信证券股份有限公司深圳深南中路中信大厦证券营业部，国泰海通证券股份有限公司上海自贸试验区第二分公司，" +
                "国金证券股份有限公司深圳分公司，国泰海通证券股份有限公司上海静安区新闸路证券营业部，国盛证券股份有限公司宁波桑田路证券营业部，平安证券股份有限公司杭州曙光路证券营业部，" +
                "国泰海通证券股份有限公司宜昌沿江大道证券营业部，招商证券股份有限公司深圳益田路免税商务大厦证券营业部，华泰证券股份有限公司苏州吴中大道证券营业部，中信证券股份有限公司浙江分公司，" +
                "东亚前海证券有限责任公司苏州分公司，广发证券股份有限公司杭州富春路证券营业部，中国银河证券股份有限公司大连黄河路证券营业部，华泰证券股份有限公司深圳前海证券营业部，" +
                "国信证券股份有限公司深圳互联网分公司，国泰海通证券股份有限公司上海浦东新区海阳西路证券营业部，东莞证券股份有限公司南京分公司，国泰海通证券股份有限公司三亚迎宾路证券营业部，" +
                "中信建投证券股份有限公司上海营口路证券营业部，浙商证券股份有限公司杭州五星路证券营业部，平安证券股份有限公司浙江分公司，爱建证券有限责任公司上海浦东新区前滩大道证券营业部，" +
                "国联民生证券股份有限公司宁波分公司，中国银河证券股份有限公司北京中关村大街证券营业部，国泰海通证券股份有限公司天津高新区梅苑路证券营业部，广发证券股份有限公司郑州农业路证券营业部，" +
                "中国国际金融股份有限公司北京建国门外大街证券营业部，华泰证券股份有限公司岳阳岳阳大道证券营业部，中国银河证券股份有限公司北京学院南路证券营业部，国新证券股份有限公司北京亦庄证券营业部，" +
                "广发证券股份有限公司深圳后海证券营业部，东吴证券股份有限公司苏州相城大道证券营业部，华泰证券股份有限公司上海武定路证券营业部，国盛证券股份有限公司杭州建设三路证券营业部，" +
                "东北证券股份有限公司佛山分公司，华源证券股份有限公司江苏分公司，国信证券股份有限公司深圳红岭中路证券营业部，东亚前海证券有限责任公司上海分公司，东方证券股份有限公司无锡梁清路证券营业部，" +
                "长江证券股份有限公司上海分公司，东方证券股份有限公司深圳金田路证券营业部，中信建投证券股份有限公司安徽分公司，国泰海通证券股份有限公司上海徐汇区宜山路证券营业部，" +
                "中信证券股份有限公司上海徐汇区漕溪北路证券营业部，中信证券股份有限公司西安朱雀大街证券营业部，中国银河证券股份有限公司绍兴鲁迅中路证券营业部，长城证券股份有限公司深圳华为稼先路证券营业部，" +
                "中泰证券股份有限公司湖北分公司，东亚前海证券有限责任公司深圳分公司，国联民生证券股份有限公司北京分公司，中信证券股份有限公司杭州延安路证券营业部，" +
                "华泰证券股份有限公司北京东三环北路证券营业部，华泰证券股份有限公司深圳益田路荣超商务中心证券营业部，东方证券股份有限公司杭州龙井路证券营业部，国泰海通证券股份有限公司深圳海岸城海德三道证券营业部，" +
                "中信建投证券股份有限公司杭州庆春路证券营业部，中泰证券股份有限公司常州惠国路证券营业部，国联民生证券股份有限公司慈溪影清路证券营业部，中信证券股份有限公司东阳吴宁西路证券营业部，" +
                "联储证券股份有限公司杭州环城北路证券营业部，国泰海通证券股份有限公司宁波广福街证券营业部，广发证券股份有限公司上海浦东新区东方路证券营业部，国泰海通证券股份有限公司余姚舜水南路证券营业部，" +
                "东方证券股份有限公司厦门仙岳路证券营业部，华泰证券股份有限公司深圳彩田路证券营业部，中信证券股份有限公司深圳滨海大道证券营业部，国元证券股份有限公司上海虹桥路证券营业部，平安证券股份有限公司深圳分公司，" +
                "中国银河证券股份有限公司厦门湖滨中路证券营业部，华泰证券股份有限公司上海普陀区江宁路证券营业部，浙商证券股份有限公司宁波分公司，中国银河证券股份有限公司宜昌新世纪证券营业部，" +
                "招商证券股份有限公司北京建国路证券营业部，东吴证券股份有限公司上海西藏南路证券营业部，国泰海通证券股份有限公司成都龙腾东路证券营业部，国泰海通证券股份有限公司重庆解放碑证券营业部，" +
                "东海证券股份有限公司福州长乐北路证券营业部，国投证券股份有限公司西安曲江池南路证券营业部，国信证券股份有限公司深圳泰然九路证券营业部，华泰证券股份有限公司上海浦东新区世纪大道证券营业部，" +
                "长江证券股份有限公司上海浦东新区东明路证券营业部，国联民生证券股份有限公司宁波财神殿南路证券营业部，中国中金财富证券有限公司湖南分公司，国泰海通证券股份有限公司无锡湖滨路证券营业部，" +
                "招商证券股份有限公司深圳深南大道车公庙证券营业部，中信证券股份有限公司上海虹口区溧阳路证券营业部，中信证券股份有限公司深圳分公司，东莞证券股份有限公司四川分公司，" +
                "中信建投证券股份有限公司北京东城分公司，甬兴证券有限公司宁波和源路证券营业部，中信证券股份有限公司杭州凤起路证券营业部，广发证券股份有限公司台州东海大道证券营业部，" +
                "华泰证券股份有限公司北京西三环国际财经中心证券营业部，华创证券有限责任公司北京西直门证券营业部，平安证券股份有限公司宁波海晏北路证券营业部，申万宏源证券有限公司深圳彩田路证券营业部，" +
                "中泰证券股份有限公司浙江分公司，方正证券股份有限公司重庆金开大道证券营业部，中信证券股份有限公司北京分公司，国投证券股份有限公司西安朱雀大街证券营业部，国信证券股份有限公司北京分公司，" +
                "联储证券股份有限公司杭州西湖桃源岭证券营业部，华林证券股份有限公司北京分公司，广发证券股份有限公司佛山顺德诚德路证券营业部，东方证券股份有限公司上海浦东新区源深路证券营业部，" +
                "国泰海通证券股份有限公司深圳福田中心区华融大厦证券营业部，华泰证券股份有限公司天津东丽开发区二纬路证券营业部，招商证券股份有限公司深圳南山南油大道证券营业部，东方证券股份有限公司东方资财，" +
                "天风证券股份有限公司上海浦东分公司，国泰海通证券股份有限公司上海长宁区天山路证券营业部，中泰证券股份有限公司深圳分公司，联储证券股份有限公司上海松林路证券营业部，兴业证券股份有限公司深圳分公司，" +
                "中信证券股份有限公司北京安外大街证券营业部，国投证券股份有限公司梅州分公司，联储证券股份有限公司宁波分公司，上海证券有限责任公司上海分公司，东亚前海证券有限责任公司浙江分公司，" +
                "中信证券股份有限公司上海闵行区万源路证券营业部，国金证券股份有限公司成都武成大街证券营业部，广发证券股份有限公司深圳福华一路证券营业部，华泰证券股份有限公司北京月坛南街证券营业部，" +
                "中国银河证券股份有限公司三亚迎宾路证券营业部，长城证券股份有限公司西北分公司，国泰海通证券股份有限公司东莞总部一路证券营业部，国投证券股份有限公司深圳红荔西路证券营业部，" +
                "华源证券股份有限公司深圳分公司，华泰证券股份有限公司深圳深南大道基金大厦证券营业部，平安证券股份有限公司深圳香蜜湖证券营业部，国泰海通证券股份有限公司南京胜利路证券营业部，" +
                "华福证券股份有限公司南京胜太路证券营业部，国泰海通证券股份有限公司上海浦东新区陆家嘴东路证券营业部，上海证券有限责任公司崇明区堡镇中路证券营业部，广发证券股份有限公司西安南广济街证券营业部，" +
                "国金证券股份有限公司上海静安区南京西路证券营业部，光大证券股份有限公司深圳深湾一路证券营业部，东吴证券股份有限公司苏州工业园区扬富路证券营业部，" +
                "国泰海通证券股份有限公司广州东风中路证券营业部，华泰证券股份有限公司江西分公司，东方证券股份有限公司张家港东环路证券营业部，中国银河证券股份有限公司上海杨浦区靖宇东路证券营业部，" +
                "国泰海通证券股份有限公司广州琶洲证券营业部，国泰海通证券股份有限公司西安雁展路证券营业部，申万宏源证券有限公司上海静安区康定路证券营业部，中信证券股份有限公司北京建国门证券营业部，" +
                "广发证券股份有限公司，国盛证券股份有限公司上海浦东新区世纪大道证券营业部，国泰海通证券股份有限公司重庆观音桥步行街证券营业部，麦高证券有限责任公司北京分公司，" +
                "平安证券股份有限公司深圳深南东路罗湖商务中心证券营业部，中国银河证券股份有限公司南京洪武路证券营业部，东方证券股份有限公司深圳深南大道证券营业部，申港证券股份有限公司四川分公司，" +
                "华泰证券股份有限公司天津河东金茂广场证券营业部，国泰海通证券股份有限公司深圳分公司华富路证券营业部，机构专用";
        String[] lhList=localLh.split("，");
        return Arrays.asList(lhList);
    }
}
