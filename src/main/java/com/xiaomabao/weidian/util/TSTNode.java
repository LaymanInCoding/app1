package com.xiaomabao.weidian.util;

/**
 * Created by de on 2016/10/12.
 */
public class TSTNode {
    /**节点的值data可以存储词原文和词性、词频等相关的信息*/
    public String data=null;
    //左中右节点
    protected TSTNode lNode;
    protected TSTNode mNode;
    protected TSTNode rNode;

    protected char splitchar;//本节点表示的字符

    protected TSTNode(char splitchar){
        this.splitchar=splitchar;
    }
    public String toString(){
        return "splitchar:"+splitchar;
    }
    /**
     * 查找的过程是：输入一个词，返回这个词对应的TSTNode对象，如果该词不在词典中，则返回空。
     * 查找词典的过程中，从树的根节点匹配Key，从前往后匹配Key*/
    protected TSTNode getNode(String key,TSTNode startNode){
        if(null==key){
            return null;
        }
        int len=key.length();
        if(len==0)
            return null;
        TSTNode currentNode=startNode;//匹配过程中当前节点的位置
        int charIndex=0;
        char cmpChar=key.charAt(charIndex);
        int charComp;
        while(true){
            if(null==currentNode){
                return null;
            }
            charComp=cmpChar-currentNode.splitchar;
            if(charComp==0){//equal
                charIndex++;
                if(charIndex==len){//找到
                    return currentNode;
                }
                else
                    cmpChar=key.charAt(charIndex);

                currentNode=currentNode.mNode;
            }else if(charComp<0){//小于
                currentNode=currentNode.lNode;
            }else {
                currentNode=currentNode.rNode;
            }
        }
    }

    /**三叉树的创建过程，就是在Trie树上创建和单词对应的节点
     * 下面方法实现的是向词典树中加入一个单词的过程*/
    TSTNode addWord(String key,TSTNode root){
        TSTNode currentNode=root;//从树的根节点开始查找
        int charIndex=0;//从词的开头匹配
        while(true){
            //比较词的当前字符与节点的当前字符
            int charComp=key.charAt(charIndex)-currentNode.splitchar;
            if(charComp==0){
                charIndex++;
                if(charIndex==key.length()){
                    currentNode.data=key;//将当前的词存到最后一个节点的data中
                    return currentNode;
                }
                //如果不存在直接后继节点
                if(currentNode.mNode==null){
                    currentNode.mNode=new TSTNode(key.charAt(charIndex));
                }
                currentNode=currentNode.mNode;
            }else if (charComp<0) {
                if(currentNode.lNode==null){
                    currentNode.lNode=new TSTNode(key.charAt(charIndex));
                }
                currentNode=currentNode.lNode;
            }else {
                if(currentNode.rNode==null){
                    currentNode.rNode=new TSTNode(key.charAt(charIndex));
                }
                currentNode=currentNode.rNode;
            }
        }
    }

    /**Trie树搜索最长匹配单词的方法
     * @param key 待匹配字符串
     * @param offset 匹配开始位置*/
    public String matchLong(String key,int offset,TSTNode root){
        String ret=null;
        if(key==null||root==null||"".equals(key)){
            return ret;
        }
        TSTNode currentNode=root;
        int charIndex=offset;
        while(true){
            if(currentNode==null){
                return ret;
            }
            int charComp=key.charAt(charIndex)-currentNode.splitchar;
            if(charComp==0){
                charIndex++;
                if(currentNode.data!=null){
                    ret=currentNode.data;//候选最长匹配词
                }
                if(charIndex==key.length()){
                    return ret;
                }
                currentNode=currentNode.mNode;
            }else if (charComp<0) {
                currentNode=currentNode.lNode;
            }else {
                currentNode=currentNode.rNode;
            }
        }
    }
    /**正向最大长度分词*/
    public void wordSegment(String sentence,TSTNode root){
        int senLen=sentence.length();
        int i=0;
        while(i<senLen){
            String word=root.matchLong(sentence, i, root);
            if(word!=null){
                //下次匹配点在这个词之后
                i+=word.length();
                //如果词在词典中，则就直接打印出来
                System.out.print(word+" ");
            }
            else{
                //如果在词典中没找到，则按单字切分
                word=sentence.substring(i, i+1);
                //打印一个字
                System.out.print(word);
                i++;//下次匹配点在这个字符之后
            }
        }
    }

}
