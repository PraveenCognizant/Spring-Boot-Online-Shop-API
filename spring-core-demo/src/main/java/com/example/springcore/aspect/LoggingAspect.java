package com.example.springcore.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * ============================================================
 * CONCEPT 18: AOP (Aspect Oriented Programming)
 * ============================================================
 *
 * AOP solves CROSS-CUTTING CONCERNS — logic that spans many classes:
 *   - Logging (every method entry/exit)
 *   - Performance monitoring (how long methods take)
 *   - Security checks
 *   - Transaction management (@Transactional is itself AOP!)
 *
 * Without AOP: copy-paste logging code in every service method.
 * With AOP: write it ONCE, apply it EVERYWHERE via pointcut expressions.
 *
 * KEY TERMS:
 *   Aspect     → This class: defines WHERE and WHAT to do
 *   Advice     → The code that runs (@Before, @After, @Around)
 *   Pointcut   → Expression matching which methods to intercept
 *   JoinPoint  → The actual method being intercepted
 *   Weaving    → Spring wraps your bean in a proxy at startup
 *
 * POINTCUT SYNTAX:
 *   execution(* com.example.springcore.service.*.*(..))
 *             │  │                                  │
 *             │  └── any class in service package   └── any parameters
 *             └── any return type
 *
 * ADVICE TYPES:
 *   @Before  → Runs BEFORE the method
 *   @After   → Runs AFTER (always, even if exception)
 *   @AfterReturning → Runs AFTER successful return (can see return value)
 *   @AfterThrowing  → Runs AFTER exception thrown
 *   @Around  → Wraps the method — most powerful, full control
 *
 * HOW IT WORKS:
 *   You call: productService.createProduct(request)
 *   Spring actually calls: AOP Proxy → @Before → real method → @After
 *
 * Requires: spring-boot-starter-aop dependency in pom.xml
 */
@Aspect
@Component
public class LoggingAspect {

    // ---- Reusable Pointcut Definitions ----

    // Matches all methods in any service implementation class
    @Pointcut("execution(* com.example.springcore.service.impl.*.*(..))")
    public void serviceLayer() {}

    // Matches all methods in any controller class
    @Pointcut("execution(* com.example.springcore.controller.*.*(..))")
    public void controllerLayer() {}

    // ---- @Before Advice: runs before service methods ----
    @Before("serviceLayer()")
    public void logBeforeServiceMethod(JoinPoint joinPoint) {
        String className  = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        System.out.println("[AOP @Before] Entering: " + className + "." + methodName + "()");
    }

    // ---- @After Advice: always runs after service methods ----
    @After("serviceLayer()")
    public void logAfterServiceMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("[AOP @After] Exiting: " + methodName + "()");
    }

    // ---- @AfterReturning: runs after successful controller methods ----
    // 'returning = "result"' binds the return value to parameter "result"
    @AfterReturning(pointcut = "controllerLayer()", returning = "result")
    public void logControllerResponse(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("[AOP @AfterReturning] Controller " + methodName + "() returned: "
                + (result != null ? result.getClass().getSimpleName() : "null"));
    }

    // ---- @Around Advice: measures execution time of ALL service methods ----
    // Most powerful: you control when the real method runs via proceed()
    @Around("serviceLayer()")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();

        try {
            // proceed() calls the REAL method — must call this or method never runs!
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;
            System.out.println("[AOP @Around] " + methodName + "() completed in " + duration + "ms");

            return result;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("[AOP @Around] " + methodName + "() FAILED after " + duration
                    + "ms — Exception: " + ex.getMessage());
            throw ex; // re-throw so normal exception handling still works
        }
    }
}
