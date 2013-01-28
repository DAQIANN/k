package org.kframework.kil.rewriter;

//unit test imports
import org.kframework.kil.Constant;
import org.kframework.kil.KApp;
import org.kframework.kil.KList;
import org.kframework.kil.Variable;
//end test imports

import org.kframework.kil.Rewrite;
import org.kframework.kil.Term;

import org.kframework.kil.matchers.Matcher;
import org.kframework.kil.matchers.MatcherException;
import org.kframework.kil.matchers.SimpleMatcher;

import org.kframework.kil.visitors.exceptions.TransformerException;

import org.kframework.compile.utils.Substitution;



/** 
 * This class implements Term Rewriting based on the
 * SimpleMatcher.  It is very dumb and slow.  Some would
 * be nicer and call it "naive" ;)
 */

public class SimpleRewriter {
  
  static Matcher matcher = new SimpleMatcher();

  //we know the cast is safe because SimpleMatcher is only defined for Terms
  //This helper function returns null if no rewrite is performed
  @SuppressWarnings("cast")
  static private Term rewriteAux(RewriteSet trs, Term t){
    Term out = null;
    for(Rewrite r : trs){
      try {
        r.getLeft().accept(matcher, t);
        Substitution substitution = new Substitution(matcher.getSubstitution());
        try {
          //ignore warning, we know this must be a Term
          out = (Term) r.getRight().accept(substitution);
          break;
        }
        catch(TransformerException te){
          System.err.println("Failed to perform substitution on rhs " + r.getRight() + " with substitution " +
              matcher.getSubstitution());
          throw new RuntimeException(te);
        }
      }
      catch(MatcherException pe){
        continue;
      }
    }
    return out;
  }

  /**
   * rewrite only rewrites one step.  Will return original term if no rewrite is performed
   *
   * @param RewriteSet trs is the term rewriting system, specified in order of rule priority
   * @param Term t is the term to rewrite one step
   */
   static public Term rewrite(RewriteSet trs, Term t){
     Term temp = rewriteAux(trs, t);
     return (temp == null)? t : temp;
   }

  /**
   *  rewrite to a normal form.  Will return original term if no rewrite is performed
   *
   * @param RewriteSet trs is the term rewriting system, specified in order of rule priority
   * @param Term t is the term to rewrite one step
   */
  static public Term rewriteToNormalForm(RewriteSet trs, Term t){
    Term out = t;
    Term temp = t;
    do {
      out = temp;
      temp = rewriteAux(trs, out);
    }
    while(temp != null);
    return out;
  }

  /**
   *  rewrite n times.  Will return original term if no rewrite is performed
   *
   * @param RewriteSet trs is the term rewriting system, specified in order of rule priority
   * @param Term t is the term to rewrite one step
   * @param int n is the (max) number of rewrites to perform
   */
  static public Term rewriteN(RewriteSet trs, Term t, int n){
    Term out = t;
    Term temp = t;
    for(int i = 0; (i <= n) && (temp != null); ++i){
      out = temp;
      temp = rewriteAux(trs, out);
    } 
    return out;
  }

  public static void main(String[] args){
    KList patternGuts = new KList();
    KList termGuts = new KList();
    KList subtermGuts = new KList();
    KList rhsGuts = new KList();
    patternGuts.add(new Variable("x", "KLabel"));
    patternGuts.add(new Variable("y", "KLabel"));
    patternGuts.add(new Variable("z", "K"));
    patternGuts.add(new Variable("x", "KLabel"));
    subtermGuts.add(Constant.KLABEL("d"));
    subtermGuts.add(Constant.KLABEL("e"));
    KApp subterm = new KApp(Constant.KLABEL("bar"), subtermGuts);
    termGuts.add(Constant.KLABEL("a"));
    termGuts.add(Constant.KLABEL("b"));
    termGuts.add(subterm);
    termGuts.add(Constant.KLABEL("a"));
    rhsGuts.add(Constant.KLABEL("42"));
    rhsGuts.add(new Variable("x", "KLabel"));
    rhsGuts.add(new Variable("z", "K"));
    KApp pattern = new KApp(Constant.KLABEL("foo"), patternGuts);
    KApp term = new KApp(Constant.KLABEL("foo"), termGuts);
    KApp rhs = new KApp(Constant.KLABEL("car"), rhsGuts);
    System.out.println(pattern);
    System.out.println(term);
    Matcher m = new SimpleMatcher();
    pattern.accept(m, term);
    System.out.println(m.getSubstitution());
    System.out.println("==================");
    RewriteSet trs = new RewriteSet();
    trs.add(new Rewrite(pattern, rhs));
    System.out.println(trs);
    System.out.println("==============>");
    System.out.println(rewrite(trs, term));
    System.out.println("==================");
    //add another rewrite that matches the rhs of the previous rewrite
    //and just rewrites to variable z
    trs.add(new Rewrite(rhs, new Variable("z", "K")));
    //add another rewrite that matches z from above (which is java variable subterm)
    trs.add(new Rewrite(subterm, Constant.KLABEL("DONE")));
    System.out.println(trs);
    System.out.println("rewrite 1: " + rewriteN(trs,term,1)); //should print same as last time
    System.out.println("rewrite 2: " + rewriteN(trs,term,2)); //should print bar(d ,, e)
    System.out.println("rewrite 3: " + rewriteN(trs,term,3)); //should print DONE
    System.out.println("rewrite normal form: " + rewriteToNormalForm(trs,term)); //should print DONE
  }
}
