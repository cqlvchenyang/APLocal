package net.lvcy.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class AP {
	//nodeOne nodeTwo
	private Map<String, Map<String, Double>> avaliability;
	//nodeTwo nodeOne
	private Map<String, Map<String, Double>> responsibility;
	private Map<String, Double> similarity;
	public AP(String srcPath) throws IOException{
		avaliability=new HashMap<String, Map<String,Double>>();
		responsibility=new HashMap<String, Map<String,Double>>();
		similarity=new HashMap<String, Double>();
		@SuppressWarnings("resource")
		BufferedReader reader=new BufferedReader(new FileReader(new File(srcPath)));
		String line=new String();
		while((line=reader.readLine())!=null){
			String[] temp=line.split("\t");
			String nodeOne=temp[0];
			String nodeTwo=temp[1];
			String nodePair=nodeOne+"-"+nodeTwo;
			Double sim=Double.valueOf(temp[2]);
			similarity.put(nodePair, sim);
			if(avaliability.containsKey(nodeOne)){
				avaliability.get(nodeOne).put(nodeTwo, 0.000);
			}else{
				Map<String, Double> tempMap=new HashMap<String, Double>();
				tempMap.put(nodeTwo, 0.000);
				avaliability.put(nodeOne, tempMap);
			}
			if(responsibility.containsKey(nodeTwo)){
				responsibility.get(nodeTwo).put(nodeOne, 0.000);
			}else{
				Map<String, Double> tempMap=new HashMap<String, Double>();
				tempMap.put(nodeOne, 0.000);
				responsibility.put(nodeTwo, tempMap);
			}
		
		}
	}
	public void iterator(int times){

		for(int i=0;i<times;i++){
			Iterator<Entry<String, Double>> iterator=similarity.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String, Double> entry=iterator.next();
				String line=entry.getKey();
				String[] nodePair=line.split("-");
				computeAvaliability(nodePair[0], nodePair[1]);
				
			}
			Iterator<Entry<String, Double>> iterator2=similarity.entrySet().iterator();
			while(iterator2.hasNext()){
				Entry<String, Double> entry=iterator2.next();
				String line=entry.getKey();
				String[] nodePair=line.split("-");
				computeResponsibility(nodePair[0], nodePair[1]);
				
			}
		}
	}
	private void computeResponsibility(String nodeOne,String nodeTwo){
		double sik=similarity.get(nodeOne+"-"+nodeTwo);
		Map<String, Double> avaliabilityMap=avaliability.get(nodeOne);
		double max=-Double.MAX_VALUE;
		Iterator<Entry<String, Double>> iterator=avaliabilityMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Double> entry=iterator.next();
			String nodeJ=entry.getKey();
			if(!nodeJ.equals(nodeTwo)){
				double aij=entry.getValue();
				double sij=similarity.get(nodeOne+"-"+nodeJ);
				double sum=aij+sij;
				if(sum>max){
					max=sum;
				}
			}
		}
		double rik=sik-max;
		responsibility.get(nodeTwo).put(nodeOne, rik);
	}
	private void computeAvaliability(String nodeOne,String nodeTwo){
		
		double rkk=responsibility.get(nodeTwo).get(nodeTwo);
		Map<String, Double> responsibilityMap=responsibility.get(nodeTwo);
		Iterator<Entry<String, Double>> iterator=responsibilityMap.entrySet().iterator();
		double sum=0;
		while(iterator.hasNext()){
			Entry<String, Double> entry=iterator.next();
			String nodeJ=entry.getKey();
			if(!nodeJ.equals(nodeTwo)&&!nodeJ.equals(nodeOne)){
				double rjk=entry.getValue();
				double max=rjk>0?rjk:0;
				sum+=max;
			}
		}
		double result=0;
		if(nodeOne.equals(nodeTwo)){
			result=sum;
		}else{
			sum+=rkk;
			result=sum>0?0:sum;
		}
		avaliability.get(nodeOne).put(nodeTwo, result);
	}
	public void resultSAR(String dstPath) throws IOException{
		BufferedWriter writer=new BufferedWriter(new FileWriter(new File(dstPath)));
		Iterator<Entry<String, Double>> iterator=similarity.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Double> entry=iterator.next();
			String[] nodePair=entry.getKey().split("-");
			double s=entry.getValue();
			double a=avaliability.get(nodePair[0]).get(nodePair[1]);
			double r=responsibility.get(nodePair[1]).get(nodePair[0]);
			writer.write(nodePair[0]+"\t"+nodePair[1]+"\t"+s+"\t"+a+"\t"+r+"\n");
		}
		writer.close();
	}
}
