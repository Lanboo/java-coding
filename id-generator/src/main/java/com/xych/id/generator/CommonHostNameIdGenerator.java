package com.xych.id.generator;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.xych.id.strategy.IdGenerateStrategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonHostNameIdGenerator implements IdGenerator {
    private IdGenerateStrategy idGenerateStrategy;
    private Class<? extends IdGenerateStrategy> strategyClazz;
    private static Lock lock = new ReentrantLock();

    public CommonHostNameIdGenerator(Class<? extends IdGenerateStrategy> strategyClazz) {
        if(strategyClazz == null) {
            throw new RuntimeException("Id Generate Strategy must provide");
        }
        this.strategyClazz = strategyClazz;
        init();
    }

    public CommonHostNameIdGenerator(IdGenerateStrategy idGenerateStrategy) {
        idGenerateStrategy.setWorkerId(getWorkerId());
        this.idGenerateStrategy = idGenerateStrategy;
    }

    private void init() {
        Long workerId = getWorkerId();
        this.idGenerateStrategy = this.getIdGenerateStrategy(workerId, this.strategyClazz);
    }

    public Long[] nextLongId(int size) {
        Long[] result = new Long[size];
        for(int i = 0; i < size; i++) {
            result[i] = nextLongId();
        }
        return result;
    }

    public Long nextLongId() {
        return Long.valueOf(Long.parseLong(this.idGenerateStrategy.nextId()));
    }

    public String[] nextStringId(int size) {
        String[] result = new String[size];
        for(int i = 0; i < size; i++) {
            result[i] = nextStringId();
        }
        return result;
    }

    public String nextStringId() {
        return this.idGenerateStrategy.nextId();
    }

    protected IdGenerateStrategy getIdGenerateStrategy(long workId, Class<? extends IdGenerateStrategy> algorithmClazz) {
        try {
            lock.lock();
            IdGenerateStrategy algorithm = (IdGenerateStrategy) algorithmClazz.newInstance();
            algorithm.setWorkerId(workId);
            return algorithm;
        }
        catch(Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
        finally {
            lock.unlock();
        }
    }

    protected Long getWorkerId() {
        String hostName = getCustHostName();
        Long workerId;
        try {
            // 提取尾部数字
            String idx = hostName.replace(hostName.replaceAll("\\d+$", ""), "");
            if("".equals(idx)) {
                // 提取所有的数字
                idx = hostName.replaceAll("[^0-9]", "");
            }
            workerId = Long.valueOf(idx);
            log.info("Parse the workerId from hostName hostName={} workid={}", hostName, workerId);
        }
        catch(NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Wrong hostname:%s, hostname must be end with number!", new Object[] { hostName }));
        }
        return workerId;
    }

    protected String getCustHostName() {
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
        }
        catch(UnknownHostException e) {
            throw new IllegalStateException("Cannot get LocalHost InetAddress, please check your network!");
        }
        String hostName = address.getHostName();
        if((hostName.equalsIgnoreCase("localhost")) || (hostName.indexOf("localhost") > 0)) {
            try {
                Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                log.info("Network interface name {} ", ni.getName());
                if(!ni.getName().contains(":")) {
                    Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                    while(inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = (InetAddress) inetAddresses.nextElement();
                        if((!inetAddress.isLoopbackAddress()) && (inetAddress.getHostAddress().indexOf(":") == -1)) {
                            hostName = inetAddress.getHostName();
                            if(hostName.indexOf("localhost") == 0) {
                                break;
                            }
                        }
                        else {
                            inetAddress = null;
                        }
                    }
                }
            }
            catch(SocketException se) {
                throw new IllegalStateException("Cannot get NetworkInterfaces, please check your network!");
            }
        }
        return hostName;
    }
}
