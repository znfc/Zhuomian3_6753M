package com.android.launcher3.t9;

import android.text.TextUtils;
import android.util.Log;

import com.android.launcher3.AppInfo;
import com.pinyinsearch.model.PinyinSearchUnit;
import com.pinyinsearch.util.PinyinUtil;
import com.pinyinsearch.util.T9Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class AppInfoHelper {
    private static final String TAG="zhao11t9";
    private static AppInfoHelper mInstance;

    private List<AppInfo> mBaseAllAppInfos; //所有应用

    //搜索结果列表
    private List<AppInfo> mT9SearchAppInfos;

    //这个变量是记录第一次搜索为空的数字串而且以后保持不变只到删除key串
    private StringBuffer mFirstNoT9SearchResultInput=null;

    public static AppInfoHelper getInstance(){
        if(null==mInstance){
            mInstance=new AppInfoHelper();
        }
        
        return mInstance;
    } 
    
    private AppInfoHelper(){
        initAppInfoHelper();
        return;
    }
    
    private void initAppInfoHelper(){
        clearAppInfoData();
        return;
    }


    public void setBaseAllAppInfos(List<AppInfo> baseAllAppInfos) {
        mBaseAllAppInfos = baseAllAppInfos;
        for(int i = 0 ; i< mBaseAllAppInfos.size();i++){
            AppInfo appInfo = mBaseAllAppInfos.get(i);
            //一定要添加下边这两句
            //这两句是将应用名字和拼音搜索的jar包联系起来的
            appInfo.getLabelPinyinSearchUnit().setBaseData(appInfo.getTitle());
            PinyinUtil.parse(appInfo.getLabelPinyinSearchUnit());
        }
    }

    public List<AppInfo> getT9SearchAppInfos() {
        return mT9SearchAppInfos;
    }

    public void t9Search(String keyword){
        List<AppInfo> baseAppInfos=getBaseAppInfo();
        Log.i(TAG, "baseAppInfos["+baseAppInfos.size()+"]");
        if(null != mT9SearchAppInfos){
            mT9SearchAppInfos.clear();
        }else{
            mT9SearchAppInfos = new ArrayList<AppInfo>();
        }

        //下边这个判断是输入数字为空时候才执行的，keyword为输入数字
        if(TextUtils.isEmpty(keyword)){
            for(AppInfo ai:baseAppInfos){
                ai.setSearchByType(AppInfo.SearchByType.SearchByNull);
                ai.clearMatchKeywords();
                ai.setMatchStartIndex(-1);
                ai.setMatchLength(0);
            }
            
            mT9SearchAppInfos.addAll(baseAppInfos);

            mFirstNoT9SearchResultInput.delete(0, mFirstNoT9SearchResultInput.length());
            Log.i(TAG, "null==search,mFirstNoT9SearchResultInput.length()="
                    + mFirstNoT9SearchResultInput.length()+","+
                    mFirstNoT9SearchResultInput);
            return;
        }

        //mFirstNoT9SearchResultInput 这个变量是记录第一次没有结果的数字串
        //这个判断是说已经有为搜索的数字穿记录了，
        if (mFirstNoT9SearchResultInput.length() > 0) {
            //再继续输入数字的话直接走这个方法来返回空的搜索结果就可以了
            if (keyword.contains(mFirstNoT9SearchResultInput.toString())) {
                Log.i(TAG,
                        "no need  to search,null!=search,mFirstNoT9SearchResultInput.length()="
                                + mFirstNoT9SearchResultInput.length() + "["
                                + mFirstNoT9SearchResultInput.toString() + "]"
                                + ";searchlen=" + keyword.length() + "["
                                + keyword + "]");
                return;
            } else {//else 就是说当前输入的keyword被删除了（就是按了退格键）
                Log.i(TAG,
                        "delete  mFirstNoT9SearchResultInput, null!=search,mFirstNoT9SearchResultInput.length()="
                                + mFirstNoT9SearchResultInput.length()
                                + "["
                                + mFirstNoT9SearchResultInput.toString()
                                + "]"
                                + ";searchlen="
                                + keyword.length()
                                + "["
                                + keyword + "]");
                //将mFirstNoT9SearchResultInput置为空
                mFirstNoT9SearchResultInput.delete(0,mFirstNoT9SearchResultInput.length());
            }
        }

        mT9SearchAppInfos.clear();
        int baseAppInfosCount=baseAppInfos.size();
        for(int i=0; i<baseAppInfosCount; i++){
            PinyinSearchUnit labelPinyinSearchUnit=baseAppInfos.get(i).getLabelPinyinSearchUnit();
            Log.i(TAG,"labelPinyinSearchUnit:"+labelPinyinSearchUnit.getPinyinUnits()+",keyword:"+keyword);
            boolean match= T9Util.match(labelPinyinSearchUnit, keyword);
            
            if (true == match) {// search by LabelPinyinUnits;
                AppInfo appInfo = baseAppInfos.get(i);
                appInfo.setSearchByType(AppInfo.SearchByType.SearchByLabel);
                appInfo.setMatchKeywords(labelPinyinSearchUnit.getMatchKeyword().toString());
                appInfo.setMatchStartIndex(appInfo.getTitle().indexOf(appInfo.getMatchKeywords().toString()));
                appInfo.setMatchLength(appInfo.getMatchKeywords().length());
                mT9SearchAppInfos.add(appInfo);

                continue;
            }
        }
        
        if (mT9SearchAppInfos.size() <= 0) {
            if (mFirstNoT9SearchResultInput.length() <= 0) {
                mFirstNoT9SearchResultInput.append(keyword);
                Log.i(TAG,
                        "no search result,null!=search,mFirstNoT9SearchResultInput.length()="
                                + mFirstNoT9SearchResultInput.length() + "["
                                + mFirstNoT9SearchResultInput.toString() + "]"
                                + ";searchlen=" + keyword.length() + "["
                                + keyword + "]");
            }
        }else{
            Collections.sort(mT9SearchAppInfos, AppInfo.mSearchComparator);
        }
        return;
    }

    private void clearAppInfoData(){
        
        if(null==mBaseAllAppInfos){
            mBaseAllAppInfos=new ArrayList<AppInfo>();
        }
        //mBaseAllAppInfos.clear();

        if(null==mT9SearchAppInfos){
            mT9SearchAppInfos=new ArrayList<AppInfo>();
        }
        mT9SearchAppInfos.clear();

        if(null==mFirstNoT9SearchResultInput){
            mFirstNoT9SearchResultInput=new StringBuffer();
        }else{
            mFirstNoT9SearchResultInput.delete(0, mFirstNoT9SearchResultInput.length());
        }
        
        return;
    }

    private List<AppInfo> getBaseAppInfo(){
        return mBaseAllAppInfos;
    }
}
