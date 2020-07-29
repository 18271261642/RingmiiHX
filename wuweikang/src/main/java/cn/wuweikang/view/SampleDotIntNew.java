package cn.wuweikang.view;

import java.util.ArrayList;
import java.util.List;

public class SampleDotIntNew {
	private static final int DON_MAX = 1500;
	private static final int INVALUE = -1;
	private int SrcSampleDot;				 //需转化的源数据点数
	private int DesSampleDot;				 //转化的目标数据点数
	private int stepCnt;
	private int srcBufCnt;
	private int desBufCnt;
	private int  preStepDot[];
	private int srcBuf[];
	private int desBuf[];
	private int needSrcDot;
	private int nPrevData;
	
//	public int getDesSampleDot() {
//		return DesSampleDot;
//	}

	public void setDesSampleDot(int desSampleDot) {
		DesSampleDot = desSampleDot;
		CalStepAndDopt();
	}


	public SampleDotIntNew(int scrSample){//, int desSample
		SrcSampleDot = scrSample;
//		DesSampleDot = desSample;
		preStepDot = new int[DON_MAX];
		srcBuf = new int[DON_MAX];
		desBuf = new int[DON_MAX];
		stepCnt = 0;	
		srcBufCnt = 0;
		desBufCnt = 0;
		needSrcDot = INVALUE;
		nPrevData = 0;
	}
	
	public void SampleDotInt(){
		srcBufCnt = 0;
		desBufCnt = 0;
		
		needSrcDot = INVALUE;
		stepCnt = 0;
		nPrevData = 0;
	}
	
	private void  CalStepAndDopt(){
		int i;
		int diffSample=0;
		
		diffSample = DesSampleDot - SrcSampleDot;

        if(diffSample < 0) //抽点
		{
			for(i = 0; i  < DesSampleDot +1; i++)
			{
				preStepDot[i] = (SrcSampleDot*(i + 1) / DesSampleDot - SrcSampleDot*i / DesSampleDot  );
			}
		}
	}
	
	
	
	public int[] SnapshotSample(int src){
		//Todo 频率一样
		    
		    if(SrcSampleDot == DesSampleDot){
		    	int des[] = new int[1];
		    	des[0] = src;
		    	return des;
		    }
		    
			CalNeedSrcDot();
				
			if(srcBufCnt < needSrcDot){
				srcBuf[srcBufCnt++] = src;
			}
			
			if(srcBufCnt >= needSrcDot){
				SnapshotFromSrcBuf();
				ResetSnapshotParameter();
			}
	
		return ReturnTheDesDot();
	}
	
	public List<Integer> SnapshotSample(int src[]){
		int des[];
		List<Integer> list = new ArrayList<Integer>();

		
		for(int i = 0; i < src.length; i++){
			des = SnapshotSample(src[i]);
			for(int j = 0; j < des.length; j++){
				list.add(des[j]);
			}
		}
		
		return list;
	}
	
	private void CalNeedSrcDot(){
		
		if(needSrcDot == INVALUE){		
			needSrcDot = preStepDot[stepCnt];
		}
	}
	

	private void SnapshotFromSrcBuf(){
	    int min = 2147483647;
	    int max = -2147483648;
	    int j = 0;
	    int k = 0;
	    int i = 0;

	    for(i = 0; i < needSrcDot; i++){
	        if(max < srcBuf[i]){
	            max = srcBuf[i];
	            k = i;
	        }

	        if(min > srcBuf[i]){
	            min = srcBuf[i];
	            j = i;
	        }

	    }

	    if (nPrevData < min)
	    {
	      min = nPrevData;
	      nPrevData = max;
	      desBuf[desBufCnt++] = min;
	      return;
	    }

	    if (nPrevData > max)
	    {
	      max = nPrevData;
	      nPrevData = min;
	      desBuf[desBufCnt++] = max;
	      return;
	    }
	    if (j < k)
	    {
	      nPrevData = max;
	       desBuf[desBufCnt++] = min;
	       return;
	    }

	    nPrevData = min;
	    desBuf[desBufCnt++] = max;
	}
	
	private void ResetSnapshotParameter(){
		srcBufCnt = 0;
		needSrcDot = INVALUE;
		stepCnt++;  /* 两组数据抽取两个点最大值和最小值*/
		if(stepCnt >= DesSampleDot){
			stepCnt = 0;
		}
	}
	
    private int[] ReturnTheDesDot( ){
    	int data[];
		int i;
		
		data = new int[desBufCnt];
	
		for(i = 0; i < desBufCnt; i++){
			data[i] = desBuf[i];
		}
		
		desBufCnt = 0;
		return data;
    }
	
	
}

