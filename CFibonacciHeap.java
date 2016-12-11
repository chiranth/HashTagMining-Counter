/**
 * Created by chiranth on 10/12/2016.
 */
/* Implementation of the Fibonacci Heap*/
import java.util.HashMap;
import java.util.Stack;


public class CFibonacciHeap {
    private CFibonacciHeapNode max; /*max pointer in the heap*/
    private HashMap name2node;      /* HashMap declaration used to store tag name as key and node address as value*/
    private Stack<CFibonacciHeapNode> delnodestack; /* Stack declaration to store the deleted nodes which will be inserted later*/

    /*Inititalization of the Heap*/
    public CFibonacciHeap()
    {
        this.max=null;
        this.name2node=new HashMap();
        this.delnodestack=new Stack<CFibonacciHeapNode>();
    }

    /*Code to insert the data into the Heap*/
    public void insert(String hashtagname,int hashtagcount)
    {
        /* Check if the hash tag is already in the heap*/
        if(name2node.containsKey(hashtagname))
        {
            /*if the hashtag is already present call the increase key function*/
            increasetagcount((CFibonacciHeapNode)name2node.get(hashtagname),hashtagcount);
        }
        else
        {
            /*insert the hash tag into the heap as a new node*/
            CFibonacciHeapNode newnode= new CFibonacciHeapNode(hashtagname,hashtagcount);
            name2node.put(hashtagname,newnode);
            if(max==null)
            {
                max=newnode;
            }
            else
            {
                /* After insert of the new node meld with the rest of the heap trees*/
                heapmeld(newnode,max);
                if(newnode.hashtagcount>max.hashtagcount)
                {
                    max=newnode;
                }
            }
        }
    }

    /* Code to meld a node with the heap after insertion and reinsertion*/
    private void heapmeld(CFibonacciHeapNode newnode, CFibonacciHeapNode max)
    {
        /*During meld point the right sibling of the new node as the right sibling of the min node and point
        the right sibling of the right node as the right sibling of the new node
          */
        newnode.right_sibling.left_sibling=max;
        max.right_sibling.left_sibling=newnode;
        CFibonacciHeapNode temp=newnode.right_sibling;
        newnode.right_sibling=max.right_sibling;
        max.right_sibling=temp;

    }

    /* Code to remove the max element in the heap*/

    public String removemax()
    {
        if(max==null) /*Check heap empty*/
            return null;
        if(max.child!=null) /*Check if the max node has any children*/
        {
            CFibonacciHeapNode temp=max.child;
            /*set the parent pointer of each of the child of max node to null before removal.*/
            while(temp.parent!=null)
            {
                temp.parent=null;
                temp=temp.right_sibling;
            }

            /* Get the left most child of the max node and meld it with the rest of the heap*/

            heapmeld(temp,max);
        }
        CFibonacciHeapNode oldmax=max;
        /* Check if heap becomes empty after the removal of max*/
        if(max.right_sibling==max)
        {
            max=null;   /*in case heap is empty set the max pointer to null*/
        }
        else
        {
            /*arbitrarily assign the max pointer*/
            max=max.right_sibling;
            detach_from_heap(oldmax);   /* cut the max pointer from the heap*/
            pairwise_combine(); /* combing the remaining trees based on their degrees*/
        }
        name2node.remove(oldmax.hashtagname); /*update the hash map to reflect the removal of the max node*/
        delnodestack.push(oldmax);  /* add the deleted node to stack for reinsertion later*/
        return oldmax.hashtagname;  /*return the hash tag name of the max node*/

    }

    /* Code to reinsert the deleted items*/

    public void reinsert_delitems()
    {
        CFibonacciHeapNode tempnode;
        /* Keep popping until the stack is empty and reinsert the nodes back to the heap*/
        while(!delnodestack.empty())
        {
            tempnode=delnodestack.pop();
            insert(tempnode.hashtagname,tempnode.hashtagcount);
        }

    }

    /* Code to detach a node from the heap of trees or from a tree*/

    private void detach_from_heap(CFibonacciHeapNode delnode)
    {
        /*replace all pointers to the deleted node in the heap with the consecutive pointers as below*/
        if(delnode.right_sibling==delnode)
            return;
        delnode.right_sibling.left_sibling=delnode.left_sibling;
        delnode.left_sibling.right_sibling=delnode.right_sibling;
        delnode.right_sibling=delnode;
        delnode.left_sibling=delnode;

    }

    /* Code to merge the trees based on their degree in the heap */

    private void pairwise_combine()
    {
        /*max degree of the tree in the heap can have upper bound of n-1 if n is the number of nodes in the heap*/
        /* Create a array which acts as degree table to store the degree of the tree in the heap*/
        CFibonacciHeapNode[] tree_table=new CFibonacciHeapNode[name2node.size()];
        /* Keep two pointers current and start to traverse through the parent pointers of the trees in the heap*/
        CFibonacciHeapNode current_node=max;
        CFibonacciHeapNode start=max;
        do{
            CFibonacciHeapNode tobeparent=current_node;
            int current_degree=current_node.degree;
            while (tree_table[current_degree]!=null)    /* start traversing the heap and update the degree table*/
            {
                CFibonacciHeapNode tobechild=tree_table[current_degree];
                /*In the trees with the same degree determine who is going to be the parent and the child in the new merged tree*/
                if(tobechild.hashtagcount>tobeparent.hashtagcount)
                {
                    CFibonacciHeapNode temp= tobechild;
                    tobechild=tobeparent;
                    tobeparent=temp;
                }
                if(tobechild==start)
                {
                    /*update the start pointer if the node at start pointer is becoming the child of the new merged tree.*/
                    start=start.right_sibling;
                }
                if(tobechild==current_node)
                {
                    /* move the current node to the left if the current node is going to become the child of the new merged tree*/
                    current_node=current_node.left_sibling;
                }
                attach_tree(tobeparent,tobechild);/*merge the trees with the same degree*/
                tree_table[current_degree++]=null;/* set the pointer in the degree table to null after the merge and proceed to te next degree*/
            }
            tree_table[current_degree]=tobeparent;/*update the degree table to point to the new merged tree*/
            current_node=current_node.right_sibling;/* continue traversing the heap by moving next*/

        }while(current_node!=start);

        max=null;
        /* After degree wise merge traverse through the degree table and update the max node*/
        for(int i=0;i<tree_table.length;i++)
        {
            if(tree_table[i]!=null)
            {
                if((max==null) || (tree_table[i].hashtagcount>max.hashtagcount))
                {
                    max=tree_table[i];
                }
            }
        }
    }

    /* Merging 2 trees given the parent pointer to be and the child pointer to be*/

    private void attach_tree(CFibonacciHeapNode tobeparent, CFibonacciHeapNode tobechild)
    {
        detach_from_heap(tobechild);    /* Remove the child to be tree from the Heap first before merge*/
        tobechild.parent=tobeparent;    /* update the parent pointer of the child to be*/
        if(tobeparent.child==null)
        {
            tobeparent.child=tobechild; /* if the parent has no children just update the child pointer*/
        }
        else
        {
            heapmeld(tobeparent.child,tobechild); /* if the parent aleady has children, meld the new node with the children nodes.*/
        }
        tobeparent.degree++;
        tobechild.is_cut=false; /*reset the child cut of the inserted child to be false*/
    }

    /* Increase key function to update the frequency of the hash tag*/
    private void increasetagcount(CFibonacciHeapNode Node2update, int additionalcount)
    {
        CFibonacciHeapNode node=Node2update;
        node.hashtagcount+=additionalcount;
        CFibonacciHeapNode parent=node.parent;
        if((parent!=null) && node.hashtagcount>parent.hashtagcount)
        {
            cutfromtree(node,parent);   /*if the updated value of the node is greater than its parent then cut the node from the tree*/
            cascadingcut(parent);   /* Continue traversing up by updating the child cut values and cutting the nodes on the way up accordingly*/
        }
        if(node.hashtagcount>max.hashtagcount)
        {
            max=node;   /*if the updated value of the node is greater than the max node then update the max node*/
        }
    }

    /* Code to cut a node from the tree*/
    private void cutfromtree(CFibonacciHeapNode child, CFibonacciHeapNode parent)
    {
        if(parent.child==child)
        {
            parent.child=child.right_sibling; /*if the child pointer is pointing to node being cut update the child pointer of the parent*/
        }
        if(child.right_sibling==child)
        {
            parent.child=null;  /* if this is the only child set the child pointer to null.*/
        }
        parent.degree--;    /*update the degree of the parent after the removal of the child*/
        detach_from_heap(child);    /* detach the node from the tree and update the sibling pointers*/
        heapmeld(child,max);    /* meld the detached node with the rest of the heap*/
        child.parent=null;
        child.is_cut=false;

    }

    /* Cut for recursively traversing upwards and see if the node needs to be detached from the tree*/

    private void cascadingcut(CFibonacciHeapNode node)
    {
        CFibonacciHeapNode parent=node.parent;
        /* Check that the node is not a root of the tree*/
        if(parent!=null)
        {
            if(node.is_cut==false)
            {
                node.is_cut=true;   /*if none of the children of the node were cut previously then set the child cut value to true*/
            }
            else
            {
                cutfromtree(node,parent); /* if there was a cut of the child of the node previosuly as well cut the node from the tree*/
                cascadingcut(parent);   /* recursively traverse up*/
            }
        }
    }
}
