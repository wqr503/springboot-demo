package com.cn.lp;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Created by qirong on 2019/6/27.
 */
@Aspect
public class TxAspect {

//    void around():call(void Hello.sayHello()){
//        System.out.println("开始事务...");
//        proceed();
//        System.out.println("事务结束...");
//    }

    @Pointcut("execution(* com.cn.lp.Hello.sayHello*(..))")
    public void sayHello() {
    }

    @Pointcut("@annotation(com.cn.lp.NeedLogin)")
    public void test() {

    }

    /**
     * 不然main也会注入切面，导致执行两次
     */
    @Pointcut("execution(* *(..))")
    public void test1() {

    }

    @Around("sayHello() && test()")
    public void onClickMethodAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature sig = joinPoint.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Object target = joinPoint.getTarget();
        System.out.println(target);
        System.out.println("开始事务...");
        System.out.println("事务结束...");
        try {
            joinPoint.proceed();
        } catch (Exception e) {
            throw e;
        }
    }

}
