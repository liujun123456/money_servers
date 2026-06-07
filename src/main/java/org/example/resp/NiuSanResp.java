package org.example.resp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NiuSanResp {
   public String name;
   public String symbol;
   public String endDate;
   public String allHolderName;
   public List<String> holderNameList;
   public List<String> pickName=new ArrayList<>();

   public List<NiuSanResp> childNiuSanList=new ArrayList<>();

}
