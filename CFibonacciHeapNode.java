/**
 * Created by chiranth on 10/12/2016.
 */

/* Class to define the Fiboncci Heap node structure*/
public class CFibonacciHeapNode {
    public String hashtagname;
    public int hashtagcount;

    public CFibonacciHeapNode parent;   /*parent pointer*/
    public CFibonacciHeapNode right_sibling;    /*left neighbour of the node*/
    public CFibonacciHeapNode left_sibling;     /*right neighbour of the node*/
    public CFibonacciHeapNode child;    /*pointer to the child of the node*/
    public int degree;  /*degree of the node*/
    public boolean is_cut;  /* boolean to record child cut values*/

    /* Initialization of a heap node*/
    CFibonacciHeapNode(String hashtagname,int hashtagcount)
    {
        this.hashtagname=hashtagname;
        this.hashtagcount=hashtagcount;
        this.parent=null;
        this.right_sibling=this;
        this.left_sibling=this;
        this.child=null;
        this.degree=0;
        this.is_cut=false;
    }
}
