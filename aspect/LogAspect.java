package com.factory.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.factory.annotation.Log;
import com.factory.entity.OperationLog;
import com.factory.service.impl.OperationLogServiceImpl;
import com.factory.utils.IpUtils;
import com.factory.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final OperationLogServiceImpl logService;
    private final ThreadPoolTaskExecutor logExecutor;
    private final ObjectMapper objectMapper;
    private static final Pattern SENSITIVE = Pattern.compile(".*(password|pwd|pass|secret|token).*", Pattern.CASE_INSENSITIVE);

    @Pointcut("execution(public * com.factory.controller.*.*(..))")
    public void controllerLog() {}

    @Around("controllerLog()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        long start = System.currentTimeMillis();
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        OperationLog op = new OperationLog();
        op.setUserId(SecurityUtils.getCurrentUserId());
        op.setUsername(SecurityUtils.getCurrentUsername());
        op.setMethod(req.getMethod() + " " + req.getRequestURI());
        op.setIp(IpUtils.getIpAddr(req));

        try {
            Object[] args = jp.getArgs();
            Object[] masked = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Map) {
                    Map<?,?> map = (Map<?,?>) args[i];
                    Map<Object,Object> m = new HashMap<>();
                    for (Map.Entry<?,?> e : map.entrySet()) {
                        m.put(e.getKey(), SENSITIVE.matcher(String.valueOf(e.getKey())).matches() ? "******" : e.getValue());
                    }
                    masked[i] = m;
                } else {
                    masked[i] = args[i];
                }
            }
            String params = objectMapper.writeValueAsString(masked);
            op.setParams(params.length() > 2000 ? params.substring(0, 2000) + "..." : params);
        } catch (Exception e) {
            op.setParams("序列化失败");
        }

        MethodSignature sig = (MethodSignature) jp.getSignature();
        Log ann = sig.getMethod().getAnnotation(Log.class);
        op.setOperation(ann != null ? ann.value() : sig.getMethod().getName());

        Object result = null;
        try {
            result = jp.proceed();
            op.setStatus(1);
            return result;
        } catch (Exception e) {
            op.setStatus(0);
            op.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            op.setDuration(System.currentTimeMillis() - start);
            op.setCreateTime(LocalDateTime.now());
            logExecutor.execute(() -> { try { logService.save(op); } catch (Exception e) { log.error("日志保存失败", e); } });
        }
    }
}