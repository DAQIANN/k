// Copyright (c) 2012-2019 K Team. All Rights Reserved.

require "substitution.md"

module LAMBDA
  imports DOMAINS
  imports SUBSTITUTION

  syntax Val ::= KVar
               | "lambda" KVar "." Exp  [binder, latex(\lambda{#1}.{#2})]
  syntax Exp ::= Val
               | Exp Exp              [strict, left]
               | "(" Exp ")"          [bracket]
  syntax KResult ::= Val

  rule (lambda X:KVar . E:Exp) V:Val => E[V / X]
endmodule
