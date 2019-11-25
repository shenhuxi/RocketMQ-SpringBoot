package com.hjmos.springbootrocketmq.aop;

import com.alibaba.fastjson.JSONObject;
import com.hjmos.springbootrocketmq.annotation.ProduceMessage;
import com.hjmos.springbootrocketmq.service.ProduceMessageService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class ProduceMessageAspect {

    @Autowired
    ProduceMessageService produceMessageService;

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    ThreadLocal<String> params = new ThreadLocal<>();

    /**
     * 用于SpEL表达式解析.
     */
    private SpelExpressionParser parser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 切入点
     */
    @Pointcut("@annotation(com.hjmos.springbootrocketmq.annotation.ProduceMessage)")
    public void controllerAspect() {
    }

    /**
     * 切入点方法执行前
     *
     * @param joinPoint 切面对象
     * @throws Throwable
     */
    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        startTime.set(System.currentTimeMillis());
        Object[] args = joinPoint.getArgs();
        Object[] arguments = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse
                    || args[i] instanceof MultipartFile) {
                continue;
            }
            arguments[i] = args[i];
        }
        String paramter = "";
        if (arguments != null) {
            try {
                paramter = JSONObject.toJSONString(arguments);
            } catch (Exception e) {
                paramter = arguments.toString();
            }
        }
        params.set(paramter);
    }

    /**
     * 成功返回后执行
     */
    @AfterReturning(returning = "ret", pointcut = "controllerAspect()")
    public void doAfterReturning(JoinPoint joinPoint, Object ret) throws Throwable {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        ProduceMessage annotation = method.getAnnotation(ProduceMessage.class);
        produceMessageService.produceMessage(annotation);
    }

    /**
     * 抛出异常时执行
     *
     * @param joinPoint
     * @param ex
     */
    @AfterThrowing(throwing = "ex", pointcut = "controllerAspect()")
    public void doAfterThrowing(JoinPoint joinPoint, Exception ex) {

        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        // 类名.方法
        String klass_method = ms.getDeclaringTypeName() + "." + ms.getName();
        log.error("异常开始位置 ： " + klass_method);
        log.error("异常信息为 ： " + ex.getMessage());
    }

    /**
     * 环切
     * @param joinPoint 切入点对象
     * @return 执行结果
     * @throws Throwable
     */
    @Around("controllerAspect()")
    public Object doBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        String content =null;
        //获取注解对象
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        ProduceMessage annotation = method.getAnnotation(ProduceMessage.class);
        //判断是否使用了spEL表达式
        if(annotation.content().contains("#"))
            content = generateKeyBySpEL(annotation.content(),joinPoint);
        else
            content = annotation.content();
        //执行目标方法
        Object resultOld = joinPoint.proceed();
        //发送消息队列
        produceMessageService.produceMessage(annotation,content);
        return resultOld;
    }

    public String generateKeyBySpEL(String spELString, ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        Expression expression = parser.parseExpression(spELString);
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        for(int i = 0 ; i < args.length ; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        return expression.getValue(context).toString();
    }
}
