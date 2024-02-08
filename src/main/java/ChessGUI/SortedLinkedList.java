/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessGUI;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Tasman James Keenan
 * studentID: 21147547
 */
public class SortedLinkedList<F extends Comparable<F>, E> {
    private class Node<F extends Comparable<F>, E> implements Comparable<Node<F,E>>{
        F key;
        E item;
        
        Node previousNode;
        Node nextNode;
        
        Node(F key, E item)
        {
            this.key = key;
            this.item = item;
        }

        @Override
        public int compareTo(Node<F,E> o) {
            return this.key.compareTo(o.key);
        }
    }
    
    
    Node head;
    Node tail;
    
    int size = 0;
    
    public SortedLinkedList()
    {
        
    };
    
    public SortedLinkedList(F key, E item)
    {
        this();
        addNode(key,item);
    }
    
    public void addNode(F key, E item)
    {
        addNode(head, new Node(key, item));
        size++;
    }
    
    public void addNode(Node head, Node node)
    {
        if (size == 0)
        {
            this.head = node;
            this.tail = node;
        } else {
            int compared = node.compareTo(head);
            if (compared < 0) //this item belongs further down the list
            {
                if (head.nextNode == null) //if we are at the end of the list
                {
                    head.nextNode = node;
                    node.previousNode = head;
                    this.tail = node;
                } else { //we are not yet at the end of the list
                    addNode(head.nextNode, node); //recursively add
                }
            } else { //we should place this Node here
                if (head.previousNode == null) //if this is the head node we have searched against
                {
                    head.previousNode = node;
                    node.nextNode = head;
                    this.head = node;
                } else { // if we are somewhere in the middle of the list
                    node.previousNode = head.previousNode;
                    node.previousNode.nextNode = node;
                    
                    node.nextNode = head;
                    head.previousNode = node; 
                }
            }
        }
    }
    
    public E[] OrderedMoves(boolean isWhite) {
        ArrayList<E> movesList = new ArrayList<>();

        Node<F, E> current = isWhite ? head : tail;

        while (current != null) {
            movesList.add(current.item);
            current = isWhite ? current.nextNode : current.previousNode;
        }

        return movesList.toArray((E[]) Array.newInstance(head.item.getClass(), movesList.size()));
    }
    
    public boolean isEmpty()
    {
        return size == 0;
    }
    
    public E getHeadItem()
    {
        return (E) head.item;
    }
    public E getTailItem()
    {
        return (E) tail.item;
    }
    
    public static void main(String[] args) {
        SortedLinkedList<Integer, String> list = new SortedLinkedList();
        
        list.addNode(2, "two");
        
        list.addNode(1, "one");
        
        list.addNode(3, "three");
        
        list.addNode(-1, "negative one");
        
        System.out.println(Arrays.toString(list.OrderedMoves(true)));
        System.out.println(Arrays.toString(list.OrderedMoves(false)));
        System.out.println(list.getHeadItem());
        System.out.println(list.getTailItem());
        
    }
}
