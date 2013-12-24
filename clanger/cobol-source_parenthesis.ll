; ModuleID = 'cobol-source_parenthesis.c'
target datalayout = "e-p:32:32:32-i1:8:8-i8:8:8-i16:16:16-i32:32:32-i64:32:64-f32:32:32-f64:32:64-v64:64:64-v128:128:128-a0:0:64-f80:32:32-n8:16:32-S128"
target triple = "i386-pc-linux-gnu"

@a = common global i32 0, align 4
@b = common global i32 0, align 4
@c = common global i32 0, align 4

; Function Attrs: nounwind
define i32 @main() #0 {
entry:
  %0 = load i32* @a, align 4
  %1 = load i32* @b, align 4
  %add = add nsw i32 %0, %1
  %2 = load i32* @c, align 4
  %sub = sub nsw i32 %add, %2
  store i32 %sub, i32* @c, align 4
  ret i32 0
}

attributes #0 = { nounwind "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf"="true" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "unsafe-fp-math"="false" "use-soft-float"="false" }
