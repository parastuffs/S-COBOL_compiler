; ModuleID = 'cobol-source.c'
target datalayout = "e-p:32:32:32-i1:8:8-i8:8:8-i16:16:16-i32:32:32-i64:32:64-f32:32:32-f64:32:64-v64:64:64-v128:128:128-a0:0:64-f80:32:32-n8:16:32-S128"
target triple = "i386-pc-linux-gnu"

@.str = private unnamed_addr constant [3 x i8] c"%d\00", align 1
@a = common global i32 0, align 4
@b = common global i32 0, align 4
@c = common global i32 0, align 4

; Function Attrs: nounwind
define i32 @main() #0 {
entry:
  %retval = alloca i32, align 4
  %d = alloca i32, align 4
  store i32 0, i32* %retval
  %call = call i32 (i8*, ...)* @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8]* @.str, i32 0, i32 0), i32* @a)
  %call1 = call i32 (i8*, ...)* @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8]* @.str, i32 0, i32 0), i32* @b)
  %0 = load i32* @a, align 4
  %1 = load i32* @b, align 4
  %cmp = icmp eq i32 %0, %1
  br i1 %cmp, label %if.then, label %if.else

if.then:                                          ; preds = %entry
  %2 = load i32* @b, align 4
  %add = add nsw i32 %2, 1
  store i32 %add, i32* @a, align 4
  br label %if.end5

if.else:                                          ; preds = %entry
  %3 = load i32* @a, align 4
  %4 = load i32* @b, align 4
  %cmp2 = icmp sgt i32 %3, %4
  br i1 %cmp2, label %if.then3, label %if.else4

if.then3:                                         ; preds = %if.else
  %5 = load i32* @b, align 4
  %sub = sub nsw i32 %5, 1
  store i32 %sub, i32* @a, align 4
  br label %if.end

if.else4:                                         ; preds = %if.else
  store i32 0, i32* @a, align 4
  br label %if.end

if.end:                                           ; preds = %if.else4, %if.then3
  br label %if.end5

if.end5:                                          ; preds = %if.end, %if.then
  %6 = load i32* @b, align 4
  %cmp6 = icmp eq i32 %6, 5
  br i1 %cmp6, label %if.then7, label %if.end8

if.then7:                                         ; preds = %if.end5
  store i32 1, i32* @b, align 4
  br label %if.end8

if.end8:                                          ; preds = %if.then7, %if.end5
  %7 = load i32* @a, align 4
  %8 = load i32* @b, align 4
  %add9 = add nsw i32 %7, %8
  %9 = load i32* @c, align 4
  %sub10 = sub nsw i32 %add9, %9
  store i32 %sub10, i32* %d, align 4
  %10 = load i32* %retval
  ret i32 %10
}

declare i32 @__isoc99_scanf(i8*, ...) #1

attributes #0 = { nounwind "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf"="true" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #1 = { "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf"="true" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "unsafe-fp-math"="false" "use-soft-float"="false" }
