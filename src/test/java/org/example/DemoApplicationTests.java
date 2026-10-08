package org.example;



import org.example.entity.*;
import org.example.mapper.*;
import org.example.req.FlowParamReq;
import org.example.req.StockReq;
import org.example.resp.HolderResp;
import org.example.service.MoneyService;
import org.example.utils.HttpUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class DemoApplicationTests {


    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private NewFlowInvestorMapper newFlowInvestorMapper;

    @Autowired
    private AllAndLastFlowMapper allAndLastFlowMapper;

    @Autowired
    private KongPanMapper kongPanMapper;

    @Autowired
    private HttpUtils utils;

    @Autowired
    private MoneyService moneyService;

    //@Test
    void contextLoads() {
        System.out.println("Spring Boot上下文加载成功！");
    }


    //@Test
    void testInsert() {
//        User user = new User();
//        user.setUsername("testUser");
//        user.setPassword("test123");
//        user.setEmail("test@example.com");
//        user.setPhone("13888888888");
//        user.setStatus(1);
//
//        int result = userMapper.insert(user);
//        System.out.println("插入结果：" + result);
//        System.out.println("生成的ID：" + user.getId());
    }

    /**
     * 入库股票代码
     */
    @Test
    void testQueryStock(){
         StockReq req=new StockReq();
        req.setApi_name("stock_basic");
        req.setToken("d8265273e779951ec1d0270da4d72b4519f66b808082df418bbf9cad");
        try {
            String result= utils.postJson("http://api.tushare.pro",req);
            List<Stock> stocks=new ArrayList<>();
            JSONObject jsonObject=new JSONObject(result);
            JSONObject data=jsonObject.getJSONObject("data");
            JSONArray jsonArray=data.getJSONArray("items");
            for (int i=0;i<jsonArray.length();i++){
                JSONArray singleStock=jsonArray.getJSONArray(i);
                Stock stock=new Stock();
                stock.setTsCode(singleStock.getString(0));
                stock.setSymbol(singleStock.getString(1));
                stock.setName(singleStock.getString(2));
                stock.setArea(singleStock.getString(3));
                stock.setIndustry(singleStock.getString(4));
                stock.setCnSpell(singleStock.getString(5));
                stock.setMarket(singleStock.getString(6));
                stock.setListDte(singleStock.getString(7));
                stock.setActName(singleStock.getString(8));
                stock.setActEntType(singleStock.getString(9));
                stocks.add(stock);

            }
            stockMapper.batchInsert(stocks);
            System.out.println(stocks.size());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 入库十大流通股东信息到数据库
     * @throws Exception
     */
    @Test
    void testQueryFlow() throws Exception {
        List<Stock> stockList=stockMapper.selectAll();
        for (int j=0;j<stockList.size();j++){
            Stock stock=stockList.get(j);
            StockReq req=new StockReq();
            req.setApi_name("top10_floatholders");
            req.setToken("d8265273e779951ec1d0270da4d72b4519f66b808082df418bbf9cad");
            FlowParamReq  flowParamReq=new FlowParamReq();
            flowParamReq.setTs_code(stock.getTsCode());
            req.setParams(flowParamReq);
            String result= utils.postJson("http://api.tushare.pro",req);

            List<NewFlowInvertor> newFlowInvertors=new ArrayList<>();
            List<FlowInvestor> flowInvestorList=new ArrayList<>();
            JSONObject jsonObject=new JSONObject(result);
            JSONObject data=jsonObject.getJSONObject("data");
            JSONArray jsonArray=data.getJSONArray("items");
            for (int i=0;i<jsonArray.length();i++){
                if (i==jsonArray.length()-1){
                    newFlowInvertors.add(dealFlowInvestor(flowInvestorList,stock));
                    flowInvestorList.clear();
                }else {
                    JSONArray singleStock=jsonArray.getJSONArray(i);
                    if (!flowInvestorList.isEmpty()&&!flowInvestorList.get(0).getEndDate().equals(singleStock.getString(2))){
                        newFlowInvertors.add(dealFlowInvestor(flowInvestorList,stock));
                        flowInvestorList.clear();
                    }

                    FlowInvestor flowInvestor=new FlowInvestor();
                    flowInvestor.setTsCode(singleStock.getString(0));
                    flowInvestor.setAnnDate(singleStock.getString(1));
                    flowInvestor.setEndDate(singleStock.getString(2));
                    flowInvestor.setHolderName(singleStock.getString(3));
//                    flowInvestor.setHoldAmount(String.format("%.0f",singleStock.getDouble(4)));
                    try {
                        flowInvestor.setHoldAmount(singleStock.getDouble(4));
                    }catch (Exception e){
                        flowInvestor.setHoldAmount(0d);
                    }

                    flowInvestor.setHoldRatio(singleStock.getString(5));
                    flowInvestor.setHoldFloatRatio(singleStock.getString(6));
                    flowInvestor.setHoldChange(singleStock.getString(7));
                    flowInvestor.setHolderType(singleStock.getString(8));
                    flowInvestor.setActEntType(singleStock.getString(8));
                    flowInvestorList.add(flowInvestor);
                }

            }

            if (!newFlowInvertors.isEmpty()){
                System.out.println(System.currentTimeMillis()+"当前插入下标---->"+j+"----股票代码---->"+stock.getTsCode()+"数据量------->"+newFlowInvertors.size());
                newFlowInvestorMapper.batchInsert(newFlowInvertors);
            }


            Thread.sleep(500);

        }

    }

    private NewFlowInvertor dealFlowInvestor(List<FlowInvestor> flowInvestorList,Stock stock){
        NewFlowInvertor newFlow=new NewFlowInvertor();
        newFlow.setTsCode(flowInvestorList.get(0).getTsCode());
        newFlow.setAnnDate(flowInvestorList.get(0).getAnnDate());
        newFlow.setEndDate(flowInvestorList.get(0).getEndDate());
        newFlow.setName(stock.getName());
        newFlow.setSymbol(stock.getSymbol());
        String allName="";
        double totalAmount=0;
        double currentAmount=0;
        for (int i=0;i<flowInvestorList.size();i++){
            if (i==flowInvestorList.size()-1){
                if (flowInvestorList.get(i).getHolderName().length()<=4){
                    allName=allName+flowInvestorList.get(i).getHolderName();
                }
                totalAmount=totalAmount+flowInvestorList.get(i).getHoldAmount();
                newFlow.setAllHolderName(allName);
                newFlow.setTotalHoldAmount(String.format("%.0f",totalAmount));
            }else {
                if (flowInvestorList.get(i).getHolderName().length()<=4){
                    allName=allName+flowInvestorList.get(i).getHolderName()+",";
                }
                totalAmount=totalAmount+flowInvestorList.get(i).getHoldAmount();
            }
            if (currentAmount==0||flowInvestorList.get(i).getHoldAmount()<currentAmount){
                currentAmount=flowInvestorList.get(i).getHoldAmount();
                newFlow.setLastHoldAmount(String.format("%.0f",flowInvestorList.get(i).getHoldAmount()));
                newFlow.setLastHoldRatio(flowInvestorList.get(i).getHoldRatio());
                newFlow.setLastHoldFloatRatio(flowInvestorList.get(i).getHoldFloatRatio());
            }
        }
        return newFlow;
    }


    /**
     * 查询10大流通持股近4个季度持续加仓
     */
    //@Test
    void queryFlowTop10(){
        List<String> stockCode=new ArrayList<>();
        List<NewFlowInvertor> flowInvertors=newFlowInvestorMapper.selectByCondition();
        String currentCode="";
        List<NewFlowInvertor> currentFlow=new ArrayList<>();
        for (int i=0;i<flowInvertors.size();i++){
            if (!currentCode.equals(flowInvertors.get(i).getSymbol())){
                String result=dealFlow(currentFlow);
                if (result!=null){
                    stockCode.add(result);
                }
                currentFlow.clear();
            }
            currentCode=flowInvertors.get(i).getSymbol();
            currentFlow.add(flowInvertors.get(i));
        }

        System.out.println(stockCode);
    }

    private String dealFlow(List<NewFlowInvertor> list){
        if (list.isEmpty()||list.size()<4){
            return null;
        }else {
            String s1=list.get(0).getLastHoldAmount();
            String s2=list.get(1).getLastHoldAmount();
            String s3=list.get(2).getLastHoldAmount();
            String s4=list.get(3).getLastHoldAmount();
            if (s1==null||s2==null||s3==null||s4==null) {
                return null;
            }
            Double one=Double.parseDouble(s1);
            Double two=Double.parseDouble(s2);
            Double three=Double.parseDouble(s3);
            Double four=Double.parseDouble(s4);
            if (one>two&&two>three&&three>four){
                return list.get(0).getSymbol();
            }
        }
        return  null;

    }


    /**
     * 入库总流通和前10大流通总量 和对应人数
     */
    //@Test
    void dealAmountFromService() throws Exception {
        List<Stock> stockList=stockMapper.selectAll();
        for (int j=2056;j<stockList.size();j++){
            Stock stock=stockList.get(j);
            StockReq req=new StockReq();
            req.setApi_name("top10_floatholders");
            req.setToken("d8265273e779951ec1d0270da4d72b4519f66b808082df418bbf9cad");
            FlowParamReq  flowParamReq=new FlowParamReq();
            flowParamReq.setTs_code(stock.getTsCode());
            flowParamReq.setStart_date("20250101");
            req.setParams(flowParamReq);
            String result= utils.postJson("http://api.tushare.pro",req);
            JSONObject jsonObject=new JSONObject(result);
            JSONObject data=jsonObject.getJSONObject("data");
            JSONArray jsonArray=data.getJSONArray("items");
            KongPan kongPan=new KongPan();
            kongPan.setSymbol(stock.getSymbol());
            kongPan.setName(stock.getName());
            double tenPersonAmount=0;
            if (jsonArray.length()>10){
                for (int i=0;i<10;i++){
                    JSONArray singleStock=jsonArray.getJSONArray(i);
                    tenPersonAmount=tenPersonAmount+singleStock.getDouble(4);
                    if (i==9){
                        try {
                            double totalAmount=(singleStock.getDouble(4)*100)/singleStock.getDouble(6);
                            kongPan.setTotalFlow(String.format("%.0f",totalAmount));
                        }catch (Exception e){
                            kongPan.setTotalFlow(null);
                        }

                    }
                }
            }
            kongPan.setTenPersonFlow(String.format("%.0f",tenPersonAmount));
            StockReq req2=new StockReq();
            req2.setApi_name("stk_holdernumber");
            req2.setToken("d8265273e779951ec1d0270da4d72b4519f66b808082df418bbf9cad");
            FlowParamReq  flowParamReq2=new FlowParamReq();
            flowParamReq2.setTs_code(stock.getTsCode());
            flowParamReq2.setStart_date("20250101");
            req2.setParams(flowParamReq2);
            String result2= utils.postJson("http://api.tushare.pro",req2);
            JSONObject jsonObject2=new JSONObject(result2);
            JSONObject data2=jsonObject2.getJSONObject("data");
            JSONArray jsonArray2=data2.getJSONArray("items");
            if (jsonArray2.length()>0&&kongPan.getTotalFlow()!=null){
                JSONArray countArray=jsonArray2.getJSONArray(0);
                kongPan.setPersonNum(countArray.getString(3));
                if (Double.parseDouble(kongPan.getTotalFlow())-Double.parseDouble(kongPan.getTenPersonFlow())<3*10000*10000){
                    kongPan.setFactor("0.2");
                }else if (Double.parseDouble(kongPan.getTotalFlow())-Double.parseDouble(kongPan.getTenPersonFlow())>3*10000*10000&&
                        Double.parseDouble(kongPan.getTotalFlow())-Double.parseDouble(kongPan.getTenPersonFlow())<6*10000*10000){
                    kongPan.setFactor("0.3");
                }else if (Double.parseDouble(kongPan.getTotalFlow())-Double.parseDouble(kongPan.getTenPersonFlow())>6*10000*10000&&
                        Double.parseDouble(kongPan.getTotalFlow())-Double.parseDouble(kongPan.getTenPersonFlow())<9*10000*10000){
                    kongPan.setFactor("0.4");
                }else if (Double.parseDouble(kongPan.getTotalFlow())-Double.parseDouble(kongPan.getTenPersonFlow())>9*10000*10000&&
                        Double.parseDouble(kongPan.getTotalFlow())-Double.parseDouble(kongPan.getTenPersonFlow())<12*10000*10000){
                    kongPan.setFactor("0.5");
                }else if (Double.parseDouble(kongPan.getTotalFlow())-Double.parseDouble(kongPan.getTenPersonFlow())>12*10000*10000&&
                        Double.parseDouble(kongPan.getTotalFlow())-Double.parseDouble(kongPan.getTenPersonFlow())<15*10000*10000){
                    kongPan.setFactor("0.6");
                }else if (Double.parseDouble(kongPan.getTotalFlow())-Double.parseDouble(kongPan.getTenPersonFlow())>15*10000*10000&&
                        Double.parseDouble(kongPan.getTotalFlow())-Double.parseDouble(kongPan.getTenPersonFlow())<20*10000*10000){
                    kongPan.setFactor("0.7");
                }else {
                    kongPan.setFactor("0");
                }
                System.out.println(System.currentTimeMillis()+"当前插入下标---->"+j+"----股票代码---->"+stock.getTsCode()+"数据量------->"+kongPan);
                kongPanMapper.insert(kongPan);
            }
            Thread.sleep(1000);
        }
    }



    /**
     * 入库2018年0101到现在 总流通股本 和前10大股本 总量，第10位流通股东持仓量
     * @throws Exception
     */
    //@Test
    void queryAllAndLastAmountToDb() throws Exception {
        List<Stock> stockList=stockMapper.selectAll();
        for (int index=2501;index<stockList.size();index++){  //遍历所有股票
            Stock stock=stockList.get(index);
            List<AllAndLastFlow> allAndLastFlows=new ArrayList<>();
           // List<HolderResp> tenHolder=queryTopTen(stock,"20180101");
            List<HolderResp> tenFlowHolder=queryTopTenFlow(stock,"20180101");

            Double lastAmount=0d;
            Double tenAmount=0d;
            Double allAmount=0d;
            String endDate="";
            for (int i=0;i<tenFlowHolder.size();i++){   //从10大流通股东集合里面取每个周期第10位股东的持股量
                HolderResp holderResp=tenFlowHolder.get(i);
                if (!endDate.isEmpty() &&!endDate.equals(holderResp.getEnd_date())){  //当前数据与上一条数据日期不符合 数据落库
                    AllAndLastFlow flow=new AllAndLastFlow();
                    flow.setSymbol(stock.getSymbol());
                    flow.setName(stock.getName());
                    flow.setTsCode(stock.getTsCode());
                    flow.setLastFlowHoldAmount(String.format("%.0f",lastAmount));
                    flow.setEndDate(endDate);
                    flow.setTenAmount(String.format("%.0f",tenAmount));
                    flow.setTotalAmount(String.format("%.0f",allAmount));
                    allAndLastFlows.add(flow);
                    lastAmount=0d;
                    tenAmount=0d;
                    allAmount=0d;
                }
                if (lastAmount==0d||holderResp.hold_amount<lastAmount){
                    lastAmount=holderResp.hold_amount;
                }
                if (allAmount==0d){
                    try {
                        allAmount=(holderResp.hold_amount*100)/holderResp.hold_ratio;
                    }catch (Exception e){

                    }
                }
                endDate=holderResp.end_date;
                tenAmount=tenAmount+holderResp.getHold_amount();
                if (i==tenFlowHolder.size()-1){  //最后一条数据  直接落库
                    AllAndLastFlow flow=new AllAndLastFlow();
                    flow.setSymbol(stock.getSymbol());
                    flow.setName(stock.getName());
                    flow.setTsCode(stock.getTsCode());
                    flow.setLastFlowHoldAmount(String.format("%.0f",lastAmount));
                    flow.setEndDate(endDate);
                    flow.setTenAmount(String.format("%.0f",tenAmount));
                    flow.setTotalAmount(String.format("%.0f",allAmount));
                    allAndLastFlows.add(flow);
                }
            }

            System.out.println(System.currentTimeMillis()+"当前插入下标---->"+index+"----股票代码---->"+stock.getTsCode()+"数据量------->"+allAndLastFlows.size());

            if (!allAndLastFlows.isEmpty()){
                allAndLastFlowMapper.batchInsert(allAndLastFlows);
            }

            Thread.sleep(1000);

        }
    }


    /**
     * 调用接口查询10大流通股东信息
     * @param stock
     * @param time
     * @return
     * @throws Exception
     */
    private List<HolderResp> queryTopTenFlow(Stock stock, String time) throws Exception {
        StockReq req=new StockReq();
        req.setApi_name("top10_floatholders");
        req.setToken("d8265273e779951ec1d0270da4d72b4519f66b808082df418bbf9cad");
        FlowParamReq  flowParamReq=new FlowParamReq();
        flowParamReq.setTs_code(stock.getTsCode());
        flowParamReq.setStart_date(time);
        req.setParams(flowParamReq);

        List<HolderResp>  list=new ArrayList<>();

        String result=utils.postJson("http://api.tushare.pro",req);
        JSONObject jsonObject=new JSONObject(result);
        JSONObject data=jsonObject.getJSONObject("data");
        JSONArray jsonArray=data.getJSONArray("items");

        for (int index=0;index<jsonArray.length();index++){
            JSONArray singleStock=jsonArray.getJSONArray(index);
            HolderResp resp=new HolderResp();
            resp.setTs_code(singleStock.getString(0));
            resp.setAnn_date(singleStock.getString(1));
            resp.setEnd_date(singleStock.getString(2));
            resp.setHolder_name(singleStock.getString(3));
            resp.setHold_amount(singleStock.getDouble(4));
            resp.setHold_ratio(singleStock.optDouble(5,0d));
            resp.setHold_float_ratio(singleStock.optDouble(6,0d));
            resp.setHold_change(singleStock.optDouble(7,0d));
            list.add(resp);
        }
        return list;
    }



    /**
     * 查询庄控盘大于百分之60 并且总流通股本小于4个亿
     */
    //@Test
    void queryZuan(){
        List<AllAndLastFlow> list=allAndLastFlowMapper.selectByTime("20260331");
        List<String> nameString=new ArrayList<>();
        for (int index=0;index<list.size();index++){
            AllAndLastFlow flow=list.get(index);
            try {
                double difference=Double.parseDouble(flow.getTotalAmount())-Double.parseDouble(flow.getTenAmount());
                if ((Double.parseDouble(flow.getLastFlowHoldAmount())*55*100/difference)>60
                        &&Double.parseDouble(flow.getTotalAmount())<400000000d
                &&!flow.getSymbol().startsWith("688")&&!flow.getSymbol().startsWith("920")){
                    nameString.add(flow.getSymbol());
                }
            }catch (Exception e){
                System.out.println(flow);
            }
        }
        System.out.println(nameString);
    }


    /**
     * 查询流通盘减去前10大股东后 盘子还在5亿到20亿之间，15-20亿股东人数在4万人以下 10-15亿股东人数在3.5万人以下   5亿到10亿3万人
     */
    //@Test
    void queryZuan2(){
        List<String> stockCode=new ArrayList<>();
        List<KongPan> list=kongPanMapper.queryAll();
        for (int i=0;i<list.size();i++){
            KongPan kongPan=list.get(i);
            Double sanZhuanFlow=Double.parseDouble(kongPan.getTotalFlow())-Double.parseDouble(kongPan.getTenPersonFlow());
            if (sanZhuanFlow>5*10000*10000&&sanZhuanFlow<20*10000*10000){
                if (sanZhuanFlow<10*10000*10000){
                    if (!kongPan.getPersonNum().equals("null")&&Integer.parseInt(kongPan.getPersonNum())<30000){
                        stockCode.add(kongPan.getSymbol());
                    }

                }else if (sanZhuanFlow>10*10000*10000&&sanZhuanFlow<15*10000*10000){
                    if (kongPan.getPersonNum()!=null&&Integer.parseInt(kongPan.getPersonNum())<35000){
                        stockCode.add(kongPan.getSymbol());
                    }
                }else if (sanZhuanFlow>15*10000*10000){
                    if (kongPan.getPersonNum()!=null&&Integer.parseInt(kongPan.getPersonNum())<40000){
                        stockCode.add(kongPan.getSymbol());
                    }
                }
            }

        }
        System.out.println(stockCode);

    }

    @Test
    void queryConnect(){
//        niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("夏重阳"));
//        niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("张素芬"));
//    }else if (type.equals("2")){
//        niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("马渲"));
//        niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("葛卫东"));
//        niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("屠文斌"));
//        niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("王孝安"));
//        niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("徐翔"));
//        niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("章建平"));
//        niuSanConnectList.addAll(niuSanConnectMapper.queryConnectByName("施玉庆"));
        moneyService.insertNiuSanConnect("施玉庆");

    }
}