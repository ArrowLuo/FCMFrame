/**
 * 
 */
package com.ccit.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ccit.bean.CenterRooter;

/**
 * @author Huaishao Luo
 * @create 2016年9月15日下午8:13:58
 */
public class FCMAlgorithm {
    
    private static final double E_6 = Math.pow(10.0, -5);
    public static Random randGen = new Random();
    
    public static void main(String[] args) {
        int data_num = 20;
        
        int clusternum = 3;
        int iternum = 50;
        int exponent = 3;
        List<double[]> datas = new ArrayList<double[]>();
        
        CenterRooter[] center_rooters = new CenterRooter[clusternum];
        for (int i = 0; i < clusternum; i++) {
            CenterRooter center_rooter = new CenterRooter();
            center_rooters[i] = center_rooter;
        }
        
        for (int i = 0; i < data_num; i++) {
            double x = (i+1)*5;
            double y = (i+1)*5;
            double[] d = new double[2];
            d[0] = x;
            d[1] = y;
            datas.add(d);
        }
        for (int i = 0; i < data_num; i++) {
            double x = (i+1)*5+10;
            double y = (i+1)*5+10;
            double[] d = new double[2];
            d[0] = x;
            d[1] = y;
            datas.add(d);
        }
        for (int i = 0; i < data_num; i++) {
            double x = (i+1)*5+15;
            double y = (i+1)*5+15;
            double[] d = new double[2];
            d[0] = x;
            d[1] = y;
            datas.add(d);
        }
        
        List<Integer> datas_label = new ArrayList<Integer>();
        for (int i = 0; i < data_num*3; i++) {
            datas_label.add(0);
        }
        
        fcm(datas, datas_label, center_rooters, clusternum, iternum, exponent);
    }

    /**
     * fcm算法
     * @param datas         原始数据
     * @param datas_label   数据标签
     * @param clusternum    类别数量
     * @param iternum       迭代次数
     * @param exponent      指数
     */
    public static void fcm(List<double[]> datas, List<Integer> datas_label, CenterRooter[] center_rooters, int clusternum, int iternum, int exponent) {
        if(datas == null || datas.size() < 1 || exponent <= 1) {
            return;
        }
        int num_data = datas.size();        // 数据行数
        int num_d = datas.get(0).length;    // 数据维数
        // 隶属度
        double[][] U = new double[clusternum][num_data];
        for (int i = 0; i < clusternum; i++) {
            for (int j = 0; j < num_data; j++) {    // 随机赋值
                U[i][j] = randGen.nextDouble()*10;
            }
        }
        
        for (int j = 0; j < num_data; j++) {        // 归一化
            double sum_d = 0;
            for (int i = 0; i < clusternum; i++) {      
                sum_d += U[i][j];
            }
            for (int i = 0; i < clusternum; i++) {      
                U[i][j] = U[i][j] / sum_d;
            }
        }
        
        /**循环--规定迭代次数作为结束条件*/
        double[][] c = new double[clusternum][num_d];
        double[] J = new double[iternum];
        for (int iter_i = 0; iter_i < iternum; iter_i++) {
            boolean brk = OneSteo(datas, center_rooters, clusternum, exponent, num_data, num_d, U, c, J, iter_i);
            if(brk) {
                break;
            }
        }
        
        for (int j = 0; j < num_data; j++) {
            int index = 0;
            double max = U[index][j];
            for (int i = 1; i < clusternum; i++) {
                if(max < U[i][j]) {
                    index = i;
                    max = U[index][j];
                }
            }
            datas_label.set(j, index+1);
        }
        
    }

    /**
     * @param datas
     * @param center_rooters
     * @param clusternum
     * @param exponent
     * @param num_data
     * @param num_d
     * @param U
     * @param c
     * @param J
     * @param iter_i
     */
    public static boolean OneSteo(List<double[]> datas, CenterRooter[] center_rooters, 
            int clusternum, int exponent, int num_data, int num_d, 
            double[][] U, double[][] c, double[] J, int iter_i) {
        
        /**计算中心值*/
        for (int j = 0; j < clusternum; j++) {
            double[] U_ij_m = new double[num_data];
            double sum_U_ij = 0;
            for (int i = 0; i < num_data; i++) {
                U_ij_m[i] = Math.pow(U[j][i], exponent);
                sum_U_ij += U_ij_m[i];
            }
            for (int k = 0; k < num_d; k++) {
                double sum_c = 0;
                for (int i = 0; i < num_data; i++) {
                    sum_c += (U_ij_m[i])*((datas.get(i))[k])/sum_U_ij;
                }
                c[j][k] = sum_c;
            }
            
            // 保存中点变动轨迹
            double[] center = new double[num_d];
            for (int ii = 0; ii < num_d; ii++) {
                center[ii] = c[j][ii];
            }
            ((center_rooters[j]).li).add(center);
        }
        
        //////////////////////////////////////////////
        //////////////////////////////////////////////
        
        /** 更新U */
        for (int j = 0; j < clusternum; j++) {
            for (int k = 0; k < num_data; k++) {
                double sum1 = 0;
                for (int j_a = 0; j_a < clusternum; j_a++) {
                    sum1 += Math.pow((norm(datas, c, k, num_d, j)/norm(datas, c, k, num_d, j_a)), 2/(exponent-1));
                }
                U[j][k] = 1/sum1;
            }
        }
        
        //////////////////////////////////////////////
        //////////////////////////////////////////////
        
        /** 计算目标J函数 */
        double sum = 0;
        for (int j = 0; j < clusternum; j++) {
            for (int k = 0; k < num_data; k++) {
                sum += Math.pow(U[j][k], exponent)*Math.pow(norm(datas, c, k, num_d, j), 2);
            }
        }
        J[iter_i] = sum;
        if((iter_i > 0) && (Math.abs(J[iter_i]-J[iter_i-1]) < E_6)) {
            return true;
        }
        return false;
    }
    
    private static double norm(List<double[]> datas, double[][] c, int k, int num_d, int j) {
        double sum = 0;
        for (int i = 0; i < num_d; i++) {
            sum += Math.pow(((datas.get(k)[i]) - c[j][i]),2);
        }
        return Math.sqrt(sum);
    }
}
