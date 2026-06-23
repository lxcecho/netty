package com.lxcecho.threadlocal;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 22:01 06-08-2022
 */
@Slf4j
public class ThreadLocalTest {

    private static final ThreadLocal<String> THREAD_NAME_LOCAL = ThreadLocal.withInitial(() -> Thread.currentThread().getName());

    private static final ThreadLocal<TradeOrder> TRADE_THREAD_LOCAL = new ThreadLocal<>();

    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            int tradeId = i;
            new Thread(() -> {
                TradeOrder tradeOrder = new TradeOrder(tradeId, tradeId % 2 == 0 ? "已支付" : "未支付");
                TRADE_THREAD_LOCAL.set(tradeOrder);
                log.info("threadName: {}", THREAD_NAME_LOCAL.get());
                log.info("tradeOrder info: {}", TRADE_THREAD_LOCAL.get());
            }, "thread-" + i).start();
        }
    }


    static class TradeOrder {
        long id;

        String status;

        public TradeOrder(long id, String status) {
            this.id = id;
            this.status = status;
        }

        @Override
        public String toString() {
            return "TradeOrder{" +
                    "id=" + id +
                    ", status='" + status + '\'' +
                    '}';
        }
    }

}
