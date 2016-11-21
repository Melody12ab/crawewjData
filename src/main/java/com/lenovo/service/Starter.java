package com.lenovo.service;

import com.lenovo.dao.FileInfoDao;
import com.lenovo.domain.FileInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by xiaobai on 15-11-14.
 */
public class Starter {

    public static void main(String[] args) {

        FileInfoDao fileInfoDao = new FileInfoDao();

//        String url="http://www.wenjian.net/0/a.html";
        String baseUrl = "http://www.wenjian.net";
        int tmp = 97;
        Document doc = null;
        boolean hasNext = true;
        for (int i = 98; i < 123; i++) {
            int urltmp = i-98==0?101:0;//起始页
            while (hasNext) {
                String infourl = baseUrl + "/" + urltmp++ + "/" + (char) i + ".html";
                try {
                    System.out.println("download " + infourl);
                    doc = Jsoup.connect(infourl).timeout(15000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                hasNext = doc.text().indexOf("该信息不存在") > -1 ? false : true;
                if(!hasNext){
                    break;
                }
                Elements infos = doc.select(".newsList  ul li a");
                int shuitemp = 0;
                for (Element info : infos) {
                    shuitemp++;
                    try {
                        FileInfo fileInfo = new FileInfo();
                        fileInfo.setFileName(info.text());
                        Document infodoc = Jsoup.connect(baseUrl + info.attr("href")).timeout(15000).userAgent("Mozilla").get();
                        Elements infodocs = infodoc.select(".f_title");
                        int sleeptime=0;
                        if(shuitemp%2==0){
                            sleeptime = (int) (6 + Math.random()*3);
                        }else{
                            sleeptime=3;
                        }
                        System.out.println("睡" + sleeptime + "s," + (urltmp - 1) + "====" + shuitemp);
                        Thread.currentThread().sleep(sleeptime * 1000);
                        if (infodocs.size() == 0) {
                            System.out.println("抓取到：" + baseUrl + info.attr("href"));
                            System.out.println("IP被封了，请切换IP");
                            System.exit(0);
                        }
                        for (Element fileff : infodocs) {
                            String temp = fileff.text();
                            if (temp.indexOf(":") > -1) {
                                if (temp.indexOf("公司") > -1) {
                                    if(temp.substring(3).trim().length()>56){
                                        fileInfo.setCompany(temp.substring(3).trim().substring(0,56));
                                    }else{
                                        fileInfo.setCompany(temp.substring(3).trim());
                                    }
                                } else if (temp.indexOf("名称") > -1) {
                                    fileInfo.setProductName(temp.substring(5).trim());
                                } else if (temp.indexOf("版本") > -1) {
                                    fileInfo.setFileVersion(temp.substring(5).trim());
                                } else if (temp.indexOf("大小") > -1) {
                                    fileInfo.setFileSize(temp.substring(5).trim());
                                } else if (temp.indexOf("路径") > -1) {
                                    fileInfo.setFilePath(temp.substring(5).trim());
                                } else if (temp.indexOf("描述") > -1) {
                                    if (temp.substring(5).trim().length() > 100) {
                                        fileInfo.setDescription(temp.substring(5).trim().substring(0, 100));
                                    } else {
                                        fileInfo.setDescription(temp.substring(5).trim());
                                    }
                                }
                            }
                        }
                        fileInfo.setDownloadUrl(baseUrl + infodoc.select("span a").last().attr("href"));
                        fileInfoDao.insertFileInfo(fileInfo);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        System.out.println("抓取到：" + baseUrl + info.attr("href"));
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
    }
}
