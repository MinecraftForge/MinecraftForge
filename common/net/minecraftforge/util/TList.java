package net.minecraftforge.util;

/**
 * Created on Jul 24, 2005
 */
import java.lang.IndexOutOfBoundsException;
import java.lang.IllegalStateException;
import java.lang.IllegalArgumentException;
import java.lang.NullPointerException;
import java.lang.CloneNotSupportedException;
import java.lang.InternalError;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Boolean;
import java.lang.System;
import java.lang.Cloneable;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.AbstractList;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.*;

/**
 * This List implementation offers an alternative to the standard Java
 * LinkedList and ArrayList implementations. A full binary tree structure is
 * used to support the list with list elements being stored in the leaves of the
 * tree in inorder. Both sequential(iterator) and indexed access to elements is
 * supported and all indexed accesses whether read-only (get(int)) or update
 * (add(int,Object), remove(int)) are achieved in O(lg(n)) time provided the
 * underlying tree structure remains balanced. A public method, rebuild(), is
 * provided to allow the programmer to re-balance the data structure whenever
 * desired. Automatic rebalancing may be enabled by coding setMode(NORMAL). When
 * NORMAL is set a heuristic is employed to automatically check if a rebuild is
 * needed after each add/remove operation and, if needed, to launch a rebuild.
 * Since rebuilding is an O(n) operation the heuristic avoids rebuilding too
 * frequently. At times if a series of add/remove operations only infrequently
 * updates the same or adjacent locations then rebuilding is counter-productive
 * because the underlying data structure tends to remain well balanced. In this
 * case setMode(RANDOM) can be coded. In cases where RANDOM mode is appropriate
 * it is dramatically faster than either LinkedList or ArrayList except for very
 * small list sizes. In NORMAL mode this List implementation outperforms both
 * LinkedList and ArrayList for large list sizes. Enhancements were added in May
 * 2009 which boost TList performance to about 8 times that of LinkList and
 * ArrayList. TLists are not particularly sensitive to where elements are added
 * except that if a large number of elements are added to the same position or
 * to adjacent positions then a rebuild my be required to achieve maximal
 * efficiency. TLists are recommended for large lists where access is random or
 * non-random. Be assured that iteration through a TList is very efficient. Two
 * additional methods, split and splice, are also of note. The former splits a
 * given TList into 2 TLists while the latter splices one TList into another.
 * Splice is similar to addALL but has been optimized for the case when the
 * input collection is a TList and is much faster than addAll.
 * 
 * **Note** The rebuild() method in this implementation rebuilds the underlying
 * full binary tree in such a way that at each node in the tree the number of
 * leaves in the left subtree differs from the number of leaves in the right
 * subtree by at most +1. Thus no part of the tree will degenerate into what is,
 * essentially, a linked list (a performance killer!). Concentrated adding of
 * elements to a single or small group of adjacent locations causes just such a
 * degeneration of the tree into a linked list. In May 2009 we enhanced the add,
 * addFirst and addLast methods, when the mode is NORMAL, to employ what we call
 * local randomization which adds an element to a randomly selected location
 * near the location specified when the method was invoked. Then using the leaf
 * chain we then are able to migrate the element the correct leaf. Locally
 * randomized additions keeps the tree shape from degenerating. Combining local
 * randomization with periodic rebuilding greatly improves performance and
 * testing shows that with large lists TLists strongly outperform ArrayLists.
 * Indeed TLists are about 8 times faster!
 * 
 * **Note** We have marked a number of methods in TList as 'final'. This was
 * done because these methods are, in their implementation details and usage,
 * highly critical to the correct operation of the TList class and many of its
 * non-final methods. Programmers with access to the source code can, of course,
 * remove these 'final' access modifiers thus enabling users of TList to
 * override these 'final' methods. However, those who wish to pursue such a
 * course should do so with great caution and resolve to test! test! test! any
 * such changes.
 * 
 * @author BOB JACKSON (re-jackson@consolidated.net).
 */
@SuppressWarnings("unused")
public class TList extends AbstractList implements List, RandomAccess,
                Cloneable, java.io.Serializable {
        /**
         * setMode(NORMAL) causes a heuristic to be used to decide whether to
         * rebuild the TList following an add or remove operation. This mode setting
         * is appropriate if many add/remove operations are performed to only a few
         * locations or to adjacent locations. NORMAL is the default.
         */
        public final static int NORMAL = 0;

        /**
         * setMode(RANDOM) specifies that no rebuild of the TList is to be performed
         * after subsequent add/remove operations. This mode is appropriate when
         * subsequent add/remove operations are expected to only infrequently update
         * the same or adjacent locations. This mode avoids the overhead of rebuild.
         */
        public final static int RANDOM = 1;

        /**
         * reference to tree root
         */
        private transient Object root; // reference to root

        /**
         * reference to first tree leaf
         */
        private transient Leaf head; // first leaf in inorder

        /**
         * reference to last tree leaf
         */
        private transient Leaf tail; // last leaf in inorder

        /**
         * number of updates since last rebuild
         */
        private transient int uprb = 0; // count of updates since last rebuild

        /**
         * mode of operation (NORMAL or RANDOM). Default NORMAL implies that a
         * heuristic will be used to decide when to rebuild. RANDOM bypasses
         * automatic rebuilds.
         */
        private int mode = NORMAL; // use heuristic to decide on rebuild

        /**
         * Randomizer. Used during add/remove operations in NORMAL mode to implement
         * local randomization. This keeps the tree shape manageble especially when
         * coupled with periodic rebuild.
         */
        private final static Random rndx = new Random();

        /**
         * used to delimit local randomization
         */
        private final static int LIMIT = 256;

        /**
         * Constructs an empty TList.
         */
        public TList() {
                root = null;
                head = null;
                tail = null;
        }

        /**
         * Ensure compatibility with ArrayList.
         */
        public TList(int initialCapacity) {
                root = null;
                head = null;
                tail = null;    
        }
        
        /**
         * Constructs a TList initialized with the elements of c.
         */
        public TList(Collection c) {
                root = null;
                head = null;
                tail = null;
                TList.this.addAll(c);
        }

        /**
         * Constructs a TList initialized with the elements of a.
         */
        public TList(Object[] a) {
                root = null;
                head = null;
                tail = null;
                TList.this.addAll(Arrays.asList(a));
                /*
                 * int len = a.length; for (int i = 0; i < len; i++) {
                 * TList.this.add(a[i]); }
                 */
        }

        /**
         * A dummy ensureCapacity method for compatibility
         * with ArrayList
         */
        public final void ensureCapacity(int minCapacity) {
        }
        
        /**
         * A dummy trimToSize method for compatibility with
         * ArrayList
         */
        public final void trimToSize() {
        }
        
        /**
         * Executes various TList test cases
         * 
         * @param args
         *            input array of Strings
         */
        public static void main(String[] args)  {

            TList t = new TList(new String[] { "Hello", "World!" });
                System.out.println(t.toString());

        } // end main

        /**
         * Returns true if there are no elements, else false
         * 
         * @return true if there are no elements, else false
         */
        public boolean isEmpty() {
                return size() == 0;
        }

        /**
         * Returns the number of elements in the List.
         * 
         * @return The number of elements in the List.
         */
        public final int size() {
                return (root == null) ? 0 : (root instanceof Leaf) ? 1 : ((Node) root)
                                .getWeight();
        }

        /**
         * Remove all elements from the TList.
         */
        public final void clear() {
                if (root == null)
                        return;
                modCount++;
                tail = null;
                head = null;
                root = null;
                uprb = 0;
        }

        /**
         * Returns a shallow copy of this TList instance. (The elements themselves
         * are not copied.)
         * 
         * @return A clone of this TList instance
         *  
         */
        public final Object clone() {
                class Q { // local work class
                        Node val = null;

                        Q next = null;
                } // end Q
                TList clone = null;
                int sz = size();
                try {
                        clone = (TList) super.clone();
                        clone.modCount = 0;
                        clone.uprb = 0;
                        clone.mode = mode;
                        clone.root = null;
                        clone.tail = null;
                        clone.head = null;
                        if (sz == 0) {
                                return clone;
                        }
                        if (sz == 1) {
                                clone.root = new Leaf((((Leaf) root)).getValue());
                                clone.head = (Leaf) (clone.root);
                                clone.tail = (Leaf) (clone.root);
                                return clone;
                        }

                        Q front = new Q();
                        front.val = new Node(sz);
                        Q back = front;
                        Q curr = front;
                        Q q = null;
                        clone.root = (Node) (front.val);

                        for (int i = 0; i < sz - 2; i++) {
                                q = new Q();
                                q.val = new Node(2); // pre-set to min wt.
                                back.next = q;
                                back = q;
                        }

                        curr = front;
                        Q posn = curr;
                        int wt = sz, wl, wr;

                        set_weights: while (wt > 3) {
                                wl = wt / 2;
                                wr = wt - wl;

                                posn = posn.next;
                                (posn.val).setWeight(wl);

                                posn = posn.next;
                                (posn.val).setWeight(wr);

                                curr = curr.next;
                                wt = (curr.val).getWeight();
                        } // end set_weights

                        curr = front;
                        posn = front;
                        Node g = null;

                        set_children: while (curr != null) {

                                g = curr.val; // never null!
                                wt = g.getWeight();
                                wl = wt / 2;
                                wr = wt - wl;

                                if (wl > 1) {
                                        posn = posn.next;
                                        g.setLeft(posn.val);
                                }

                                if (wr > 1) {
                                        posn = posn.next;
                                        g.setRight(posn.val);
                                }

                                curr = curr.next;
                        } // end set_children

                        Object node = null, xnode = null;
                        Leaf nxlf = new Leaf();
                        Leaf pv = null, v = null;
                        nxlf.setRight(head);
                        int index = 0, first, last, pivot, wn;

                        set_leaves: do {

                                node = (Node) clone.root;
                                wn = sz;
                                first = 0;
                                last = sz;

                                while (wn > 4) {

                                        pivot = first;
                                        xnode = ((Node) node).getLeft();
                                        wn = ((Node) xnode).getWeight();
                                        pivot += wn;

                                        if (index < pivot) {
                                                last = pivot;
                                                node = xnode;
                                        } else {
                                                first = pivot;
                                                node = (Node) (((Node) node).getRight());
                                                wn = ((Node) node).getWeight();
                                        }
                                } // end while (wn>4)

                                if (wn == 4) {
                                        xnode = (Node) (((Node) node).getLeft());
                                        nxlf = nxlf.getRight();
                                        pv = v;
                                        v = new Leaf((((Leaf) nxlf)).getValue());
                                        if (pv == null)
                                                clone.head = v;
                                        else {
                                                v.setLeft(pv);
                                                pv.setRight(v);
                                        }
                                        ((Node) xnode).setLeft(v);
                                        index++;
                                        nxlf = nxlf.getRight();
                                        pv = v;
                                        v = new Leaf((((Leaf) nxlf)).getValue());
                                        if (pv == null)
                                                clone.head = v;
                                        else {
                                                v.setLeft(pv);
                                                pv.setRight(v);
                                        }
                                        ((Node) xnode).setRight(v);
                                        index++;
                                        node = (Node) (((Node) node).getRight());
                                }
                                if (wn == 3) {
                                        nxlf = nxlf.getRight();
                                        pv = v;
                                        v = new Leaf((((Leaf) nxlf)).getValue());
                                        if (pv == null)
                                                clone.head = v;
                                        else {
                                                v.setLeft(pv);
                                                pv.setRight(v);
                                        }
                                        ((Node) node).setLeft(v);
                                        index++;
                                        node = (Node) (((Node) node).getRight());
                                }
                                nxlf = nxlf.getRight();
                                pv = v;
                                v = new Leaf((((Leaf) nxlf)).getValue());
                                if (pv == null)
                                        clone.head = v;
                                else {
                                        v.setLeft(pv);
                                        pv.setRight(v);
                                }
                                ((Node) node).setLeft(v);
                                index++;
                                nxlf = nxlf.getRight();
                                pv = v;
                                v = new Leaf((((Leaf) nxlf)).getValue());
                                if (pv == null)
                                        clone.head = v;
                                else {
                                        v.setLeft(pv);
                                        pv.setRight(v);
                                }
                                ((Node) node).setRight(v);
                                index++;

                        } while (index < sz);

                        clone.tail = v;

                        return clone;
                } catch (CloneNotSupportedException e) {
                        throw new InternalError();
                }
        }

        /**
         * Performs a function similar to addAll(index,collection) but optimized for
         * the case when the input collection is a TList. When the splice operation
         * is completed the TList specified by the parameter 'other' will have an
         * empty state, i.e. other.isEmpty() will return 'true'. Note, however, that
         * the elements previously contained in the 'other' TList will nevertheless
         * be contained in this TList just before the element which had index
         * 'whereIndex' before the splice operation began.
         * 
         * @param whereIndex
         *            the index where the other TList will be spliced.
         * @param other
         *            the TList to be spliced into this TList at whereIndex.
         * @throws IndexOutOfBoundsException
         * @throws NullPointerException
         * @throws IllegalArgumentException
         */
        public final void splice(int whereIndex, TList other)
                        throws IndexOutOfBoundsException, NullPointerException,
                        IllegalArgumentException {
                if (whereIndex < 0 || whereIndex > this.size())
                        throw new IndexOutOfBoundsException();
                if (other == null)
                        throw new NullPointerException();
                if (other == this || !(other instanceof TList))
                        throw new IllegalArgumentException();

                this.rebuildTest();
                other.rebuildTest();

                if (other.size() == 0) {
                        return;
                }

                if (this.size() == 0) {
                        this.root = other.root;
                        this.head = other.head;
                        this.tail = other.tail;
                        other.clear();
                        this.modCount++;
                        return;
                }

                if (whereIndex == 0) {
                        other.splice(other.size(), this);
                        this.root = other.root;
                        this.head = other.head;
                        this.tail = other.tail;
                        other.clear();
                        this.rebuildTest();
                        this.modCount++;
                        return;
                }

                if (whereIndex == this.size()) {
                        int sz = this.size() + other.size();
                        Node z = new Node(sz);
                        z.setLeft(this.root);
                        z.setRight(other.root);
                        this.root = z;
                        z = null;
                        (this.tail).setRight(other.head);
                        (other.head).setLeft(this.tail);
                        this.tail = other.tail;
                        other.clear();
                        this.rebuildTest();
                        this.modCount++;
                        return;
                }

                TList tlist2 = this.split(whereIndex);
                this.splice(this.size(), other);
                this.splice(this.size(), tlist2);
                return;

        }

        /**
         * Splits this TList into 2 TLists. Upon return this TList will contain only
         * those elements with index less than whereIndex. Those elements with index
         * equal to or greater than whereIndex will be moved to a new TList which is
         * returned to the caller.
         * 
         * @param whereIndex
         *            the position where this TList will be split
         * @return a TList containing the elements of this TList which had an index
         *         equal to or greater than whereIndex in this TList.
         * @throws IndexOutOfBoundsException
         */
        public final TList split(int whereIndex) throws IndexOutOfBoundsException {
                if (whereIndex < 0 || whereIndex > this.size())
                        throw new IndexOutOfBoundsException();

                TList retTList = new TList();
                retTList.mode = this.mode;

                if (whereIndex == this.size()) {
                        this.rebuildTest();
                        return retTList;
                }

                if (whereIndex == 0) {
                        retTList.root = this.root;
                        retTList.head = this.head;
                        retTList.tail = this.tail;
                        retTList.uprb = this.uprb;
                        this.clear();
                        retTList.rebuildTest();
                        return retTList;
                }

                retTList = getTList(this.size() - whereIndex, this.getLeaf(whereIndex));
                this.removeRange(whereIndex, this.size());

                return retTList;
        }

        /**
         * Constructs a sublist or view over this list beginning at fromindex and
         * including all indexes greater than fromIndex which are less than toIndex.
         * If fromIndex=toIndex the list is empty. A sub-sublist may also be
         * constructed on top of a sublist.
         * 
         * @param fromIndex
         *            Index into the base list where this sublist begins.
         * @param toIndex
         *            The index into the base list which is 1 greater than the
         *            largest base index in the sublist.
         * @return a sublist of this list from fromIndex up to but not including
         *         toIndex.
         */
        public final List subList(int fromIndex, int toIndex) {
                return new SubTList(this, fromIndex, toIndex);
        }

        /**
         * Returns an array whose elements are equal to the elements of this list in
         * the same order as would be returned by an iterator over this list.
         * 
         * @return an array whose elements are the elements of this list in the same
         *         order as would returned by an iterator over this list.
         */
        public Object[] toArray() {
                int s = size();
                Object[] o = new Object[s];
                Iterator iter = iterator();
                int i = 0;
                while (iter.hasNext()) {
                        o[i++] = iter.next();
                }
                return o;
        }

        /**
         * Returns an array whose elements are equal to the elements of this list in
         * the same order as would be returned by an iterator over this list.
         * 
         * @param o
         *            The input array which is to be filled (if possible) with
         *            elements of this list and returned. If the input array is too
         *            small a new array is allocated with the same runtime type as
         *            o, filled, and returned. If o is too large o[size()] is set to
         *            null.
         * @return an array whose elements are the elements of this list in the same
         *         order as would returned by an iterator over this list. The
         *         runtime type of the returned array is the same as the type as the
         *         input array o.
         * @throws NullPointerException
         *             if the input o is null.
         */
        public Object[] toArray(Object[] o) throws NullPointerException {
                if (o == null)
                        throw new NullPointerException();
                int s = size();
                if (o.length < s)
                        o = (Object[]) java.lang.reflect.Array.newInstance(o.getClass()
                                        .getComponentType(), s);
                Iterator iter = iterator();
                int i = 0;
                while (iter.hasNext()) {
                        o[i++] = iter.next();
                }
                if (o.length > s)
                        o[s] = null;
                return o;
        }

        /**
         * Returns true if the list contains the specified element.
         * 
         * @param o
         *            element whose presence in the list is to be tested.
         * @return true if the list contains the specified element.
         */
        public boolean contains(Object o) {
                Iterator iter = iterator();
                while (iter.hasNext()) {
                        Object o2 = iter.next();
                        if (o == null) {
                                if (o2 == null)
                                        return true;
                        } else { // o != null
                                if (o2 != null)
                                        if (o2.equals(o))
                                                return true;
                        }
                }
                return false;
        } // end contains

        /**
         * Returns the index of the first occurance of an element in this list which
         * matches the input. If no match is found returns -1
         * 
         * @param o
         *            Element whose first index in the list is to be returned.
         * @return The first index of the input element in the list.
         */
        public int indexOf(Object o) {
                ListIterator lter = listIterator();
                if (o == null) {
                        while (lter.hasNext())
                                if (lter.next() == null)
                                        return lter.previousIndex();
                } else {
                        while (lter.hasNext())
                                if (o.equals(lter.next()))
                                        return lter.previousIndex();
                }
                return -1;
        }

        /**
         * Returns the index of the last occurance of an element in this list which
         * matches the input. If no match is found returns -1
         * 
         * @param o
         *            Element whose last index in the list is to be returned.
         * @return The last index of the input element in the list.
         */
        public int lastIndexOf(Object o) {
                ListIterator lter = listIterator(size());
                if (o == null) {
                        while (lter.hasPrevious())
                                if (lter.previous() == null)
                                        return lter.nextIndex();
                } else {
                        while (lter.hasPrevious())
                                if (o.equals(lter.previous()))
                                        return lter.nextIndex();
                }
                return -1;
        }

        /**
         * Returns true if the list contains all elements in specified collection.
         * 
         * @param c
         *            collection whose elements are to be tested for presence in
         *            this list.
         * @return true if the list contains all the elements in the specified
         *         collection.
         * @throws NullPointerException
         *             if the input collection is null.
         */
        public boolean containsAll(Collection c) throws NullPointerException {
                if (c == null)
                        throw new NullPointerException();
                Iterator iter = c.iterator();
                while (iter.hasNext()) {
                        if (!this.contains(iter.next()))
                                return false;
                }
                return true;
        }

        /**
         * Add a new element to the end of the List. Size() increases by 1.
         * 
         * @param o
         *            element to be added to the end of the List.
         * @return true.
         */
        public boolean add(Object o) {
                addLast(o); // increments modCount
                return true;
        }

        /**
         * Add a collection of elements to the end of the List. size() increases by
         * c.size().
         * 
         * @param c
         *            Collection of elements to be added to the end of the list.
         * @return true.
         * @throws NullPointerException
         *             if the input collection is null.
         */
        public boolean addAll(Collection c) throws NullPointerException {
                if (c == null)
                        throw new NullPointerException();
                if (c.isEmpty())
                        return false;

                /*
                 * if (c instanceof TList) { splice(this.size(), (TList) c); return
                 * true; }
                 */

                Iterator iter = c.iterator();
                while (iter.hasNext()) {
                        this.addLast(iter.next()); // increments modCount
                }
                return true;
        }

        /**
         * Add a collection of elements to the List at the specified position.
         * size() increases by c.size().
         * 
         * @param index
         *            position in list in front of which the elements of the
         *            Collection c are to be added.
         * @param c
         *            Collection of elements to be added to the list at the
         *            indicated position.
         * @return true if any elements are added to the list.
         * @throws NullPointerException
         *             if the input collection is null.
         * @throws IndexOutOfBoundException
         *             if index is less than 0 or greater than size().
         */
        public boolean addAll(int index, Collection c) throws NullPointerException,
                        IndexOutOfBoundsException {
                if (index < 0 || index > size())
                        throw new IndexOutOfBoundsException();
                if (c == null)
                        throw new NullPointerException();
                if (c.isEmpty())
                        return false;

                /*
                 * if (c instanceof TList) { splice(index, (TList) c); return true; }
                 */

                Iterator iter = c.iterator();
                int i = index;
                // ListIterator lter = listIterator(index);
                while (iter.hasNext()) {
                        // lter.add(iter.next());
                        this.add(i++, iter.next()); // increments modCount
                }
                return true;
        }

        /**
         * Add a new element to the end of the List. Size() increases by 1.
         * 
         * @param o
         *            element to be added to the end of the List.
         */
        public final void addLast(Object o) {

                if (root == null) {
                        root = new Leaf(o);
                        head = (Leaf) root;
                        tail = head;
                        modCount++;
                        return;
                } // end if

                if (root instanceof Leaf) {
                        Object r = new Node(2); // make a new root
                        ((Node) r).setLeft(root); // make old root its left child
                        Leaf rl = new Leaf(o); // create new leaf
                        ((Node) r).setRight(rl); // make it right child of r
                        head = (Leaf) root;
                        tail = (Leaf) rl;
                        head.setRight(tail);
                        tail.setLeft(head);
                        root = r;
                        modCount++;
                        return;
                } // end if

                if (mode == NORMAL) {
                        int index = size();
                        int ix0 = index - LIMIT, ix1 = index;
                        if (ix0 < 0)
                                ix0 = 0;
                        int ixx = ix0 + rndx.nextInt(1 + (ix1 - ix0));
                        if (ixx != index) {
                                Leaf lf, lg;
                                if (ixx == 0)
                                        lf = xaddFirst(null); // modCount++
                                else
                                        lf = xadd(ixx, null); // modCount++

                                // ixx < index
                                for (int k = ixx; k < index; k++, lf = lg) {
                                        lg = lf.getRight();
                                        lf.setValue(lg.getValue());
                                }

                                lf.setValue(o);
                                return;
                        }
                }

                modCount++;

                uprb += 1;

                Object p = root; // save reference

                Object node = root; // begin at root
                while (node instanceof Node) {
                        ((Node) node).setWeight(((Node) node).getWeight() + 1); // increment
                        p = node; // save reference to parent
                        node = ((Node) node).getRight();
                } // endwhile
                /*
                 * node is now the last leaf; we make a new leaf to the right of node
                 * which will be the new last leaf.
                 */
                Object q = new Node(2); // make a new parent
                ((Node) q).setLeft(node); // make node the left child
                Leaf rl = new Leaf(o); // create new leaf
                ((Node) q).setRight(rl); // make it the right child of q
                tail = (Leaf) rl; // tail is new leaf
                tail.setLeft((Leaf) node);
                ((Leaf) node).setRight(tail);
                ((Node) p).setRight(q); // make q the parent's right child
                rebuildTest();

        } // end addLast

        /**
         * Called in NORMAL mode during local randomization.
         * 
         * @param o
         *            new object to be added last in list.
         * @return Leaf containing o.
         */
        private final Leaf xaddLast(Object o) {

                modCount++;

                uprb += 1;

                Object p = root; // save reference

                Object node = root; // begin at root
                while (node instanceof Node) {
                        ((Node) node).setWeight(((Node) node).getWeight() + 1); // increment
                        p = node; // save reference to parent
                        node = ((Node) node).getRight();
                } // endwhile
                /*
                 * node is now the last leaf; we make a new leaf to the right of node
                 * which will be the new last leaf.
                 */
                Object q = new Node(2); // make a new parent
                ((Node) q).setLeft(node); // make node the left child
                Leaf rl = new Leaf(o); // create new leaf
                ((Node) q).setRight(rl); // make it the right child of q
                tail = (Leaf) rl; // tail is new leaf
                tail.setLeft((Leaf) node);
                ((Leaf) node).setRight(tail);
                ((Node) p).setRight(q); // make q the parent's right child
                rebuildTest();
                return rl;

        } // end xaddLast

        /**
         * Add a new element to the front of the List. All original elements are
         * shifted over by 1 and thus their indices are increased by 1. Size()
         * increases by 1.
         * 
         * @param o
         *            element to be added to the front of the List.
         */
        public final void addFirst(Object o) {

                if (root == null) {
                        root = new Leaf(o);
                        head = (Leaf) root;
                        tail = head;
                        modCount++;
                        return;
                } // end if

                if (root instanceof Leaf) {
                        Object r = new Node(2); // make a new root
                        ((Node) r).setRight(root); // make root its right child
                        Leaf rl = new Leaf(o); // create a new leaf
                        ((Node) r).setLeft(rl); // make it left child of q
                        head = (Leaf) rl;
                        tail = (Leaf) root;
                        head.setRight(tail);
                        tail.setLeft(head);
                        root = r;
                        modCount++;
                        return;
                } // end if

                if (mode == NORMAL) {
                        int index = 0;
                        int ix0 = 0, ix1 = LIMIT;
                        if (ix1 > size())
                                ix1 = size();
                        int ixx = ix0 + rndx.nextInt(1 + (ix1 - ix0));
                        if (ixx != index) {
                                Leaf lf, lg;
                                if (ixx == size())
                                        lf = xaddLast(null); // modCount++
                                else
                                        lf = xadd(ixx, null); // modCount++

                                // ixx > index
                                for (int k = ixx; k > index; k--, lf = lg) {
                                        lg = lf.getLeft();
                                        lf.setValue(lg.getValue());
                                }
                                lf.setValue(o);
                                return;
                        }
                }

                modCount++;

                uprb += 1;

                Object p = root; // save reference

                Object node = root; // begin at root
                while (node instanceof Node) {
                        ((Node) node).setWeight(((Node) node).getWeight() + 1); // increment
                        p = node; // save reference to parent
                        node = ((Node) node).getLeft();
                } // endwhile
                /*
                 * node is now the first leaf; we make a new leaf to the left of node
                 * which will be the new first leaf.
                 */
                Object q = new Node(2); // make a new parent
                ((Node) q).setRight(node); // make node the right child
                Leaf rl = new Leaf(o); // create a new leaf
                ((Node) q).setLeft(rl); // make a new left child of q
                head = (Leaf) rl; // head is new leaf
                head.setRight((Leaf) node);
                ((Leaf) node).setLeft(head);
                ((Node) p).setLeft(q); // make q the parent's left child
                rebuildTest();
        } // end addFirst

        /**
         * Called in NORMAL mode during local randomization.
         * 
         * @param o
         *            new object to be added first in list.
         * @return Leaf containing o.
         */
        private final Leaf xaddFirst(Object o) {

                modCount++;

                uprb += 1;

                Object p = root; // save reference

                Object node = root; // begin at root
                while (node instanceof Node) {
                        ((Node) node).setWeight(((Node) node).getWeight() + 1); // increment
                        p = node; // save reference to parent
                        node = ((Node) node).getLeft();
                } // endwhile
                /*
                 * node is now the first leaf; we make a new leaf to the left of node
                 * which will be the new first leaf.
                 */
                Object q = new Node(2); // make a new parent
                ((Node) q).setRight(node); // make node the right child
                Leaf rl = new Leaf(o); // create a new leaf
                ((Node) q).setLeft(rl); // make a new left child of q
                head = (Leaf) rl; // head is new leaf
                head.setRight((Leaf) node);
                ((Leaf) node).setLeft(head);
                ((Node) p).setLeft(q); // make q the parent's left child
                rebuildTest();
                return rl;

        } // end xaddFirst

        /**
         * Add a new element to the List at the specified position. The element
         * originally at the specified position and all following elements are
         * shifted over by 1 position and thus their indices are increased by 1.
         * Size() increases by 1.
         * 
         * @param index
         *            position where the new element is to be inserted. If index =
         *            size() calls addLast(Object o).
         * @param o
         *            new element to be inserted in the List.
         * @throws IndexOutOfBoundsException
         *             if (index < 0 || index > size()).
         */
        public final void add(int index, Object o) throws IndexOutOfBoundsException {

                if (index < 0 || index > size())
                        throw new IndexOutOfBoundsException();

                if (root == null) {
                        root = new Leaf(o);
                        head = (Leaf) root;
                        tail = head;
                        modCount++;
                        return;
                }

                if (index == size()) {
                        addLast(o); // increments modCount
                        return;
                }

                if (index == 0) {
                        addFirst(o); // increments modCount
                        return;
                } // end if

                if (mode == NORMAL) {
                        int ix0 = index - LIMIT, ix1 = index + LIMIT;
                        if (ix0 < 0)
                                ix0 = 0;
                        if (ix1 > size())
                                ix1 = size();
                        int ixx = ix0 + rndx.nextInt(1 + (ix1 - ix0));
                        if (ixx != index) {
                                Leaf lf, lg;
                                if (ixx == size())
                                        lf = xaddLast(null); // modCount++
                                else if (ixx == 0)
                                        lf = xaddFirst(null); // modCount++
                                else
                                        lf = xadd(ixx, null); //modCount++
                                if (ixx < index)
                                        for (int k = ixx; k < index; k++, lf = lg) {
                                                lg = lf.getRight();
                                                lf.setValue(lg.getValue());
                                        }
                                else
                                        // ixx > index
                                        for (int k = ixx; k > index; k--, lf = lg) {
                                                lg = lf.getLeft();
                                                lf.setValue(lg.getValue());
                                        }
                                lf.setValue(o);
                                return;
                        }
                }

                Object p = root; // save reference

                Object node = root; // begin at root

                int first = 0, last = 0;

                modCount++;

                uprb += 1;

                last += size();

                while (node instanceof Node) {

                        ((Node) node).setWeight(((Node) node).getWeight() + 1); // increment
                        p = node; // save reference to parent Node

                        int pivot = first;

                        if (((Node) node).getLeft() instanceof Node)
                                pivot += ((Node) (((Node) node).getLeft())).getWeight();
                        else
                                pivot += 1; // i.e., left child is a leaf

                        if (index < pivot) {
                                last = pivot;
                                node = ((Node) node).getLeft();
                        } else {
                                first = pivot;
                                node = ((Node) node).getRight();
                        }
                } // endwhile

                /*
                 * node is the Leaf in front of which we must insert a new Leaf. To do
                 * this we create a new parent and a new Leaf making the new leaf the
                 * left-child of the new parent and making node its right-child. We then
                 * make the new parent the left or right child of node's original
                 * parent.
                 */

                Object q = new Node(2); // make a new parent
                ((Node) q).setRight(node); // make node the right child
                Leaf rl = (Leaf) (((Leaf) node).getLeft()); // ref to node's left
                Leaf r = new Leaf(o); // make a new leaf
                ((Node) q).setLeft(r); // make it the left child of q
                ((Leaf) node).setLeft((Leaf) r); // link node to
                // new
                ((Leaf) r).setRight((Leaf) node); // link new to
                // node
                if (node != head) { // 
                        ((Leaf) rl).setRight((Leaf) r);
                        ((Leaf) r).setLeft((Leaf) rl);
                } else
                        head = (Leaf) r; // make new leaf the new head

                if (node == ((Node) p).getLeft()) // node is a left child
                        ((Node) p).setLeft(q);
                else
                        // node is a right child
                        ((Node) p).setRight(q);

                rebuildTest();
        } // end add

        /**
         * Called in NORMAL mode during local randomization.
         * 
         * @param index
         *            where new object (o) is to be added.
         * @param o
         *            new object to be added.
         * @return Leaf containing o.
         */
        private final Leaf xadd(int index, Object o) {

                Object p = root; // save reference

                Object node = root; // begin at root

                int first = 0, last = 0;

                modCount++;

                uprb += 1;

                last += size();

                while (node instanceof Node) {

                        ((Node) node).setWeight(((Node) node).getWeight() + 1); // increment
                        p = node; // save reference to parent Node

                        int pivot = first;

                        if (((Node) node).getLeft() instanceof Node)
                                pivot += ((Node) (((Node) node).getLeft())).getWeight();
                        else
                                pivot += 1; // i.e., left child is a leaf

                        if (index < pivot) {
                                last = pivot;
                                node = ((Node) node).getLeft();
                        } else {
                                first = pivot;
                                node = ((Node) node).getRight();
                        }
                } // endwhile

                /*
                 * node is the Leaf in front of which we must insert a new Leaf. To do
                 * this we create a new parent and a new Leaf making the new leaf the
                 * left-child of the new parent and making node its right-child. We then
                 * make the new parent the left or right child of node's original
                 * parent.
                 */

                Object q = new Node(2); // make a new parent
                ((Node) q).setRight(node); // make node the right child
                Leaf rl = (Leaf) (((Leaf) node).getLeft()); // ref to node's left
                Leaf r = new Leaf(o); // make a new leaf
                ((Node) q).setLeft(r); // make it the left child of q
                ((Leaf) node).setLeft((Leaf) r); // link node to
                // new
                ((Leaf) r).setRight((Leaf) node); // link new to
                // node
                if (node != head) { // 
                        ((Leaf) rl).setRight((Leaf) r);
                        ((Leaf) r).setLeft((Leaf) rl);
                } else
                        head = (Leaf) r; // make new leaf the new head

                if (node == ((Node) p).getLeft()) // node is a left child
                        ((Node) p).setLeft(q);
                else
                        // node is a right child
                        ((Node) p).setRight(q);

                rebuildTest();

                return r;
        } // end xadd

        /**
         * Replaces the element at the indicated position with the specified
         * element.
         * 
         * @param index
         *            position of element to be replaced
         * @param o
         *            new element to replace the old one
         * @return element previously at the specified position
         * @throws IndexOutOfBoundsException.
         */
        public final Object set(int index, Object o)
                        throws IndexOutOfBoundsException {
                if (index < 0 || index >= size())
                        throw new IndexOutOfBoundsException();
                // modCount++;
                Leaf lf = getLeaf(index);
                Object v = lf.getValue();
                lf.setValue(o);
                return v;
        }

        /**
         * Sets the processing mode to NORMAL or RANDOM.
         * 
         * @param mode
         *            new processing mode.
         * @throws IllegalArgumentException
         *             if input mode is not NORMAL or RANDOM.
         */
        public final void setMode(int mode) throws IllegalArgumentException {
                if (mode != NORMAL && mode != RANDOM)
                        throw new IllegalArgumentException();
                if (mode != this.mode) {
                        rebuild();
                        this.mode = mode;
                }
        }

        /**
         * Returns the current processing mode (NORMAL|RANDOM)
         * 
         * @return NORMAL or RANDOM
         */
        public int getMode() {
                return mode;
        }

        /**
         * Returns the element of the List located at the position indicated by
         * parameter index.
         * 
         * @param index
         *            Position of element to return.
         * @return Element stored at indicated position.
         * @throws IndexOutOfBoundsException
         *             if (index < 0 || index >= size()).
         */
        public final Object get(int index) throws IndexOutOfBoundsException {

                Object node = root;
                int first = 0, last = 0;

                if (index < 0 || index >= size())
                        throw new IndexOutOfBoundsException();

                if (root instanceof Leaf)
                        return ((Leaf) root).getValue();

                last += size();

                while (node instanceof Node) {

                        int pivot = first;

                        if (((Node) node).getLeft() instanceof Node)
                                pivot += ((Node) (((Node) node).getLeft())).getWeight();
                        else
                                pivot += 1;

                        if (index < pivot) {
                                last = pivot;
                                node = ((Node) node).getLeft();
                        } else {
                                first = pivot;
                                node = ((Node) node).getRight();
                        }
                } // endwhile
                return ((Leaf) node).getValue();
        } // end get

        /**
         * Returns the first element of the List.
         * 
         * @return First element in the List.
         * @throws NoSuchElementException
         *             if the List is empty.
         */
        public final Object getFirst() throws NoSuchElementException {

                if (root == null)
                        throw new NoSuchElementException();

                Object node = root;

                if (root instanceof Leaf)
                        return ((Leaf) root).getValue();

                while (node instanceof Node) {
                        node = ((Node) node).getLeft();
                } // endwhile

                return ((Leaf) node).getValue();
        } // end getFirst

        /**
         * Returns the last element of the List.
         * 
         * @return Last element in the List.
         * @throws NoSuchElementException
         *             if the List is empty.
         */
        public final Object getLast() throws NoSuchElementException {
                if (root == null)
                        throw new NoSuchElementException();

                Object node = root;

                if (root instanceof Leaf)
                        return ((Leaf) root).getValue();

                while (node instanceof Node) {
                        node = ((Node) node).getRight();
                } // endwhile

                return ((Leaf) node).getValue();
        }

        /**
         * Returns the leaf at the position indicated by parameter index. Private
         * use only.
         * 
         * @param index
         *            position of leaf to return.
         * @return Leaf at indicated position.
         * @throws IndexOutOfBoundsException
         *             if (index < 0 || index >= size()).
         */
        private final Leaf getLeaf(int index) throws IndexOutOfBoundsException {

                Object node = root;
                int first = 0, last = 0;

                if (index < 0 || index >= size())
                        throw new IndexOutOfBoundsException();

                if (root instanceof Leaf)
                        return ((Leaf) root);

                last += size();

                while (node instanceof Node) {

                        int pivot = first;

                        if (((Node) node).getLeft() instanceof Node)
                                pivot += ((Node) (((Node) node).getLeft())).getWeight();
                        else
                                pivot += 1;

                        if (index < pivot) {
                                last = pivot;
                                node = ((Node) node).getLeft();
                        } else {
                                first = pivot;
                                node = ((Node) node).getRight();
                        }
                } // endwhile
                return ((Leaf) node);
        } // end getLeaf

        /**
         * Builds and returns a TList of size = listSize given a reference firstLeaf
         * to the first leaf in a chain of leaves containing listSize leaves.
         * 
         * @param listSize
         *            Output TList's size.
         * @param firstLeaf
         *            First leaf in a chain of listSize leaves.
         * @return A TList of size listSize.
         * @throws IllegalArgumentException.
         */
        private final TList getTList(int listSize, Leaf firstLeaf)
                        throws IllegalArgumentException {
                class Q { // local work class
                        Node val = null;

                        Q next = null;
                } // end Q
                if (firstLeaf != null)
                        if (!(firstLeaf instanceof Leaf))
                                throw new IllegalArgumentException();
                if (listSize < 0)
                        throw new IllegalArgumentException();

                TList tlist = new TList();
                if ((listSize == 0) || (firstLeaf == null))
                        return tlist;
                if (listSize == 1) {
                        tlist.root = firstLeaf;
                        tlist.head = firstLeaf;
                        tlist.tail = firstLeaf;
                        tlist.head.setLeft(null);
                        tlist.tail.setRight(null);
                        return tlist;
                }
                int sz = listSize;
                Q front = new Q();
                front.val = new Node(sz);
                Q back = front;
                Q curr = front;
                Q q = null;
                tlist.root = (Node) (front.val);

                for (int i = 0; i < sz - 2; i++) {
                        q = new Q();
                        q.val = new Node(2); // pre-set to min wt.
                        back.next = q;
                        back = q;
                }

                curr = front;
                Q posn = curr;
                int wt = sz, wl, wr;

                set_weights: while (wt > 3) {
                        wl = wt / 2;
                        wr = wt - wl;

                        posn = posn.next;
                        (posn.val).setWeight(wl);

                        posn = posn.next;
                        (posn.val).setWeight(wr);

                        curr = curr.next;
                        wt = (curr.val).getWeight();
                } // end set_weights

                curr = front;
                posn = front;
                Node g = null;

                set_children: while (curr != null) {

                        g = curr.val; // never null!
                        wt = g.getWeight();
                        wl = wt / 2;
                        wr = wt - wl;

                        if (wl > 1) {
                                posn = posn.next;
                                g.setLeft(posn.val);
                        }

                        if (wr > 1) {
                                posn = posn.next;
                                g.setRight(posn.val);
                        }

                        curr = curr.next;
                } // end set_children

                Object node = null, xnode = null;
                Leaf nxlf = new Leaf();
                Leaf pv = null, v = null;
                nxlf.setRight(firstLeaf);
                int index = 0, first, last, pivot, wn;

                set_leaves: do {

                        node = (Node) tlist.root;
                        wn = sz;
                        first = 0;
                        last = sz;

                        while (wn > 4) {

                                pivot = first;
                                xnode = ((Node) node).getLeft();
                                wn = ((Node) xnode).getWeight();
                                pivot += wn;

                                if (index < pivot) {
                                        last = pivot;
                                        node = xnode;
                                } else {
                                        first = pivot;
                                        node = (Node) (((Node) node).getRight());
                                        wn = ((Node) node).getWeight();
                                }
                        } // end while (wn>4)

                        if (wn == 4) {
                                xnode = (Node) (((Node) node).getLeft());
                                nxlf = nxlf.getRight();
                                pv = v;
                                v = nxlf;
                                if (pv == null)
                                        tlist.head = v;
                                else {
                                        v.setLeft(pv);
                                        pv.setRight(v);
                                }
                                ((Node) xnode).setLeft(v);
                                index++;
                                nxlf = nxlf.getRight();
                                pv = v;
                                v = nxlf;
                                if (pv == null)
                                        tlist.head = v;
                                else {
                                        v.setLeft(pv);
                                        pv.setRight(v);
                                }
                                ((Node) xnode).setRight(v);
                                index++;
                                node = (Node) (((Node) node).getRight());
                        }
                        if (wn == 3) {
                                nxlf = nxlf.getRight();
                                pv = v;
                                v = nxlf;
                                if (pv == null)
                                        tlist.head = v;
                                else {
                                        v.setLeft(pv);
                                        pv.setRight(v);
                                }
                                ((Node) node).setLeft(v);
                                index++;
                                node = (Node) (((Node) node).getRight());
                        }
                        nxlf = nxlf.getRight();
                        pv = v;
                        v = nxlf;
                        if (pv == null)
                                tlist.head = v;
                        else {
                                v.setLeft(pv);
                                pv.setRight(v);
                        }
                        ((Node) node).setLeft(v);
                        index++;
                        nxlf = nxlf.getRight();
                        pv = v;
                        v = nxlf;
                        if (pv == null)
                                tlist.head = v;
                        else {
                                v.setLeft(pv);
                                pv.setRight(v);
                        }
                        ((Node) node).setRight(v);
                        index++;

                } while (index < sz);

                tlist.tail = v;
                tlist.head.setLeft(null);
                tlist.tail.setRight(null);

                return tlist;
        }

        /**
         * Removes the first ocurrence of the specified element in this list.
         * 
         * @param o
         *            Object to look for and remove (if found) in this list.
         * @return true if the specified element is found in this list.
         */
        public boolean remove(Object o) {
                Iterator iter = this.iterator();
                boolean changed = false;
                Object o2 = null;
                while (iter.hasNext()) {
                        o2 = iter.next();
                        if (o == null) {
                                if (o2 == null) {
                                        iter.remove(); // increments modCount
                                        // changed = true;
                                        return true;
                                }
                        } else { // o != null
                                if (o2 != null)
                                        if (o2.equals(o)) {
                                                iter.remove(); // increments modCount
                                                // changed = true;
                                                return true;
                                        }
                        }
                }
                // return changed;
                return false;
        }

        /**
         * Removes all occurances from this list of any element in the specified
         * collection.
         * 
         * @param c
         *            the input collection whose elements are to be removed fom this
         *            list.
         * @return true if this list is changed.
         * @throws NullPointerException
         *             if the input collection is null.
         */
        public boolean removeAll(Collection c) throws NullPointerException {
                if (c == null)
                        throw new NullPointerException();
                if (c.size() < 1)
                        return false;
                
                boolean changed = false;
                Iterator iter = this.iterator();
                while (iter.hasNext()) {
                        if (c.contains(iter.next())) {
                                iter.remove(); // increments modCount
                                changed = true;
                        }
                }
                return changed;
        }

        /**
         * Removes all occurances from this list of any element not in the specified
         * collection.
         * 
         * @param c
         *            the input collection whose elements are not to be removed fom
         *            this list.
         * @return true if this list is changed.
         * @throws NullPointerException
         *             if the input collection is null.
         */
        public boolean retainAll(Collection c) throws NullPointerException {
                if (c == null)
                        throw new NullPointerException();
                boolean changed = false;
                Iterator iter = this.iterator();
                while (iter.hasNext()) {
                        if (!c.contains(iter.next())) {
                                iter.remove(); // increments modCount
                                changed = true;
                        }
                }
                return changed;
        }

        /**
         * Remove the element in the List at the specified position. The elements
         * following the removed element are shifted back by 1 position and thus
         * their indices are decreased by 1. Size() decreases by 1.
         * 
         * @param index
         *            position of element to be removed from the List.
         * @return The element removed from the List.
         * @throws IndexOutOfBoundsException
         *             if (index < 0 || index >= size()).
         */
        public final Object remove(int index) throws IndexOutOfBoundsException {

                if (index < 0 || index >= size())
                        throw new IndexOutOfBoundsException();

                if (root instanceof Leaf) {
                        Object v = ((Leaf) root).getValue();
                        clear(); // increments modCount
                        return v;
                }

                /*
                 * if (ixx != index) { Leaf lf = xremove(ixx), lg; Object o =
                 * lf.getValue(), v; if (ixx < index) for (int k = ixx; k < index; k++,
                 * lf = lg, o = v) { lg = lf.getRight(); v = lg.getValue();
                 * lg.setValue(o); } else // ixx > index for (int k = ixx; k > index;
                 * k--, lf = lg, o = v) { lg = lf.getLeft(); v = lg.getValue();
                 * lg.setValue(o); } return v; }
                 */

                Object g = root; // save reference
                Object p = root; // save reference

                Object node = root; // begin at root
                int first = 0, last = 0;

                modCount++;

                uprb += 1;

                last += size();

                while (node instanceof Node) {

                        ((Node) node).setWeight(((Node) node).getWeight() - 1); // decrement

                        g = p; // save reference to grandparent
                        p = node; // save reference to parent

                        int pivot = first;

                        if (((Node) node).getLeft() instanceof Node)
                                pivot += ((Node) (((Node) node).getLeft())).getWeight();
                        else
                                pivot += 1;

                        if (index < pivot) {
                                last = pivot;
                                node = ((Node) node).getLeft();
                        } else {
                                first = pivot;
                                node = ((Node) node).getRight();
                        }
                } // endwhile

                /*
                 * node is the leaf we must delete. To do this we set its grandparent's
                 * left reference or right reference equal to its sybling.
                 */

                Object v = ((Leaf) node).getValue(); // extract value from Leaf

                if (g == p) { // i.e., node is left or right child of the root

                        if (node == ((Node) p).getLeft()) { // node is left child of root
                                head = (Leaf) (((Leaf) node).getRight());
                                /* head.setLeft(null); */
                                root = ((Node) p).getRight(); // node's sybling
                        } else { // node is right child of root
                                tail = (Leaf) (((Leaf) node).getLeft());
                                /* tail.setRight(null); */
                                root = ((Node) p).getLeft(); // node's sybling
                        }
                        rebuildTest();
                        return v;
                }

                Leaf ll = (Leaf) (((Leaf) node).getLeft());
                Leaf rl = (Leaf) (((Leaf) node).getRight());

                if (node == head) {
                        /* rl.setLeft(null); */
                        head = rl;
                } else if (node == tail) {
                        /* ll.setRight(null); */
                        tail = ll;
                } else {
                        rl.setLeft(ll);
                        ll.setRight(rl);
                }

                if (node == ((Node) p).getLeft()) // node is left child of p

                        if (p == ((Node) g).getLeft()) // p is left child of g
                                ((Node) g).setLeft(((Node) p).getRight()); // node's sybling
                        else
                                // p is right child of g
                                ((Node) g).setRight(((Node) p).getRight()); // node's sybling

                else // node is right child of p

                if (p == ((Node) g).getLeft()) // p is left child of g
                        ((Node) g).setLeft(((Node) p).getLeft()); // node's sybling
                else
                        // p is right child of g
                        ((Node) g).setRight(((Node) p).getLeft()); // node's sybling
                rebuildTest();
                return v;

        } // end remove

        /**
         * Remove the first element in the List. The elements following the removed
         * element are shifted back by 1 position and thus their indices are
         * decreased by 1. Size() decreases by 1.
         * 
         * @return The element removed from the List.
         * @throws NoSuchElementException
         *             if size()=0.
         */
        public final Object removeFirst() throws NoSuchElementException {

                if (root == null)
                        throw new NoSuchElementException();

                if (root instanceof Leaf) {
                        Object v = ((Leaf) root).getValue();
                        clear(); // increments modCount
                        return v;
                }

                modCount++;

                uprb += 1;

                Object g = root; // save reference
                Object p = root; // save reference

                Object node = root; // begin at root
                while (node instanceof Node) {
                        ((Node) node).setWeight(((Node) node).getWeight() - 1); // decrement
                        g = p; // save reference to grandparent
                        p = node; // save reference to parent
                        node = ((Node) node).getLeft();
                } // endwhile

                Object v = ((Leaf) node).getValue(); // extract value from Leaf

                if (g == p) {
                        // node is left child of root
                        head = (Leaf) (((Leaf) node).getRight());
                        /* head.setLeft(null); */
                        root = ((Node) p).getRight(); // node's sybling
                        rebuildTest();
                        return v;
                }

                //  head=node
                Leaf rl = (Leaf) (((Leaf) node).getRight());
                /* rl.setLeft(null); */
                head = rl;

                // node is left child of p
                if (p == ((Node) g).getLeft()) // p is left child of g
                        ((Node) g).setLeft(((Node) p).getRight()); // node's sybling
                else
                        // p is right child of g
                        ((Node) g).setRight(((Node) p).getRight()); // node's sybling

                rebuildTest();
                return v;
        } // end removeFirst

        /**
         * Remove the last element in the List. Size() decreases by 1.
         * 
         * @return The element removed from the List.
         * @throws NoSuchElementException
         *             if size()=0.
         */
        public final Object removeLast() throws NoSuchElementException {
                if (root == null)
                        throw new NoSuchElementException();

                if (root instanceof Leaf) {
                        Object v = ((Leaf) root).getValue();
                        clear(); // increments modCount
                        return v;
                }

                modCount++;

                uprb += 1;

                Object g = root; // save reference
                Object p = root; // save reference

                Object node = root; // begin at root
                while (node instanceof Node) {
                        ((Node) node).setWeight(((Node) node).getWeight() - 1); // decrement
                        g = p; // save reference to grandparent
                        p = node; // save reference to parent
                        node = ((Node) node).getRight();
                } // endwhile

                Object v = ((Leaf) node).getValue(); // extract value from Leaf

                if (g == p) {
                        // node is right child of root
                        tail = (Leaf) (((Leaf) node).getRight());
                        /* tail.setRight(null); */
                        root = ((Node) p).getLeft(); // node's sybling
                        rebuildTest();
                        return v;
                }

                //  tail=node
                Leaf ll = (Leaf) (((Leaf) node).getLeft());
                /* ll.setRight(null); */
                tail = ll;

                // node is right child of p
                if (p == ((Node) g).getLeft()) // p is left child of g
                        ((Node) g).setLeft(((Node) p).getLeft()); // node's sybling
                else
                        // p is right child of g
                        ((Node) g).setRight(((Node) p).getLeft()); // node's sybling

                rebuildTest();
                return v;

        }

        /**
         * Removes from this list any element whose index is not less than fromIndex
         * and is less than toIndex.
         * 
         * @param fromIndex
         *            smallest index in the range.
         * @param toIndex
         *            index which is 1 greater than any index in the range.
         * @throws IndexOutOfBoundsException
         *             if fromIndex is less than 0 or if toIndex is greater than
         *             size().
         * @throws IllegalArgumentException
         *             if fromIndex is greater than toIndex.
         */
        public final void removeRange(int fromIndex, int toIndex)
                        throws IndexOutOfBoundsException, IllegalArgumentException {
                if (fromIndex < 0 || toIndex > size())
                        throw new IndexOutOfBoundsException();

                if (fromIndex > toIndex)
                        throw new IllegalArgumentException();

                if (fromIndex == toIndex)
                        return;

                if (fromIndex == 0 && toIndex == size()) {
                        clear(); // increments modCount
                        return;
                }

                TList tl1 = null, tl2 = null;
                int sz = size(), wt = sz - (toIndex - fromIndex);
                int i1 = fromIndex, i2 = sz - toIndex;
                modCount++;

                if (toIndex == sz) {
                        tl1 = getTList(i1, head);
                        root = tl1.root;
                        head = tl1.head;
                        head.setLeft(null);
                        tail = tl1.tail;
                        tail.setRight(null);
                        if (root instanceof Node)
                                ((Node) root).setWeight(wt);
                        rebuild();
                        return;
                }
                if (fromIndex == 0) {
                        tl2 = getTList(i2, getLeaf(toIndex));
                        root = tl2.root;
                        head = tl2.head;
                        head.setLeft(null);
                        tail = tl2.tail;
                        tail.setRight(null);
                        if (root instanceof Node)
                                ((Node) root).setWeight(wt);
                        rebuild();
                        return;
                }

                tl1 = getTList(i1, head);
                tl2 = getTList(i2, getLeaf(toIndex));
                ((Node) root).setLeft(tl1.root);
                ((Node) root).setRight(tl2.root);
                ((Node) root).setWeight(wt);
                head = tl1.head;
                head.setLeft(null);
                tail = tl2.tail;
                tail.setRight(null);
                (tl1.tail).setRight(tl2.head);
                (tl2.head).setLeft(tl1.tail);
                rebuild();
                return;

        }

        /**
         * Returns a bi-directional ListIterator over this list.
         * 
         * @return a bi-directional ListIterator over this list.
         */
        public final ListIterator listIterator() {
                return listIterator(0);
        }

        /**
         * Returns a bi-directional ListIterator over this list starting at the
         * indicated position.
         * 
         * @param position
         *            location where the iterator is initially positioned.
         * @return a bi-directional ListIterator over this list starting at the
         *         indicated position.
         * @throws IndexOutOfBoundsException.
         */
        public final ListIterator listIterator(final int position)
                        throws IndexOutOfBoundsException {

                if (position < 0 || position > size())
                        throw new IndexOutOfBoundsException();

                return new ListIterator() {

                        Leaf curr;

                        Leaf prev;

                        int index;

                        boolean ncall;

                        boolean pcall;

                        boolean acall;

                        boolean rcall;

                        int expectedModCount;

                        {
                                index = position;
                                if (index == size())
                                        curr = null;
                                else
                                        curr = getLeaf(index);
                                if (index == 0)
                                        prev = null;
                                else
                                        prev = getLeaf(index - 1);
                                ncall = false;
                                pcall = false;
                                acall = false;
                                rcall = false;
                                expectedModCount = modCount;
                        }

                        public final boolean hasPrevious() {
                                return prev != null;
                        }

                        public final Object next() throws NoSuchElementException {

                                /*
                                 * if (index == size()) throw new NoSuchElementException();
                                 */
                                if (expectedModCount != modCount)
                                        throw new ConcurrentModificationException();
                                try {
                                        //      Object v = get(index++);
                                        Object v = curr.getValue();
                                        index++;
                                        prev = curr;
                                        if (index == size()) {
                                                curr = null;
                                        } else {
                                                curr = prev.getRight();
                                        }
                                        ncall = true;
                                        pcall = false;
                                        acall = false;
                                        rcall = false;
                                        return v;
                                } catch (IndexOutOfBoundsException e) {
                                        if (expectedModCount != modCount)
                                                throw new ConcurrentModificationException();
                                        throw new NoSuchElementException();
                                }
                        }

                        public final int nextIndex() {
                                return index;
                        }

                        public final Object previous() throws NoSuchElementException {

                                /*
                                 * if (index == 0) throw new NoSuchElementException();
                                 */
                                if (expectedModCount != modCount)
                                        throw new ConcurrentModificationException();
                                try {
                                        //      Object v = get(--index);
                                        --index;
                                        Object v = prev.getValue();
                                        curr = prev;
                                        if (index == 0) {
                                                prev = null;
                                        } else {
                                                prev = curr.getLeft();
                                        }
                                        pcall = true;
                                        ncall = false;
                                        acall = false;
                                        rcall = false;
                                        return v;
                                } catch (IndexOutOfBoundsException e) {
                                        if (expectedModCount != modCount)
                                                throw new ConcurrentModificationException();
                                        throw new NoSuchElementException();
                                }
                        }

                        public final int previousIndex() {
                                return index - 1;
                        }

                        public final void remove() throws IllegalStateException {

                                if ((ncall == false) && (pcall == false))
                                        throw new IllegalStateException();
                                if (expectedModCount != modCount)
                                        throw new ConcurrentModificationException();
                                try {
                                        if (ncall) {
                                                TList.this.remove(--index); // prev
                                                expectedModCount = modCount;
                                                if (index == 0)
                                                        prev = null;
                                                else
                                                        prev = prev.getLeft();
                                        } else { // pcall
                                                TList.this.remove(index); // curr
                                                expectedModCount = modCount;
                                                if (index == size())
                                                        curr = null;
                                                else
                                                        curr = curr.getRight();
                                        }
                                        rcall = true;
                                        ncall = false;
                                        pcall = false;
                                        acall = false;
                                } catch (IndexOutOfBoundsException e) {
                                        throw new ConcurrentModificationException();
                                }
                        }

                        public final boolean hasNext() {
                                return curr != null;
                        }

                        public final void add(Object o) {
                                if (expectedModCount != modCount)
                                        throw new ConcurrentModificationException();
                                try {
                                        TList.this.add(index, o); // increments modCount
                                        expectedModCount = modCount;
                                        index++;
                                        // if ((curr == null) || (size() == 1))
                                        if (curr == null)
                                                prev = TList.this.tail;
                                        else {
                                                curr = TList.this.getLeaf(index);
                                                prev = curr.getLeft();
                                        }
                                        /*
                                         * prev = getLeaf(index++); if (index == size()) { curr =
                                         * null; // prev = tail; } else curr = getLeaf(index);
                                         */
                                        acall = true;
                                        ncall = false;
                                        pcall = false;
                                        rcall = false;
                                } catch (IndexOutOfBoundsException e) {
                                        throw new ConcurrentModificationException();
                                }
                        }

                        public final void set(Object o) throws IllegalStateException {

                                if ((ncall == false) && (pcall == false))
                                        throw new IllegalStateException();
                                if (expectedModCount != modCount)
                                        throw new ConcurrentModificationException();

                                try {
                                        if (ncall)
                                                //      TList.this.set(index - 1, o);
                                                prev.setValue(o);
                                        else
                                                // pcall
                                                //      TList.this.set(index, o);
                                                curr.setValue(o);
                                        //      expectedModCount = modCount;
                                } catch (IndexOutOfBoundsException e) {
                                        throw new ConcurrentModificationException();
                                }

                        } // end set

                }; // end of returned anonymous class

        } // end of listIterator method

        /**
         * Returns a (forward direction only) iterator over this list.
         * 
         * @return a forward Iterator over this list.
         */
        public final Iterator iterator() {
                return new Iterator() {

                        Leaf curr;

                        int index;

                        boolean ncall;

                        int expectedModCount;

                        {
                                index = 0;
                                if (size() == 0)
                                        curr = null;
                                else
                                        curr = head;
                                //      curr = getLeaf(index);
                                ncall = false;
                                expectedModCount = modCount;
                        }

                        public final void remove() throws IllegalStateException {

                                if (ncall == false)
                                        throw new IllegalStateException();
                                if (expectedModCount != modCount)
                                        throw new ConcurrentModificationException();

                                try {
                                        ncall = false;
                                        TList.this.remove(--index); // increments modCount
                                        expectedModCount = modCount;
                                        // if (index == size())
                                        //      curr = null;
                                        // else
                                        //      curr = getLeaf(index);

                                } catch (IndexOutOfBoundsException e) {
                                        throw new ConcurrentModificationException();
                                }
                        }

                        public final boolean hasNext() {
                                return curr != null;
                        }

                        public final Object next() throws NoSuchElementException {

                                /*
                                 * if (index == size()) throw new NoSuchElementException();
                                 */
                                if (expectedModCount != modCount)
                                        throw new ConcurrentModificationException();

                                try {
                                        //      Object v = get(index++);
                                        Object v = curr.getValue();
                                        index++;
                                        if (index == size())
                                                curr = null;
                                        else
                                                curr = curr.getRight();
                                        ncall = true;
                                        return v;
                                } catch (IndexOutOfBoundsException e) {
                                        if (expectedModCount != modCount)
                                                throw new ConcurrentModificationException();
                                        throw new NoSuchElementException();
                                }
                        }
                };
        }

        /**
         * Save the state of the TList instance to a stream (that is, serialize it).
         * 
         * @param s
         *            The java.io.ObjectOutputStream to which the TList is to be
         *            serialized.
         * @serialData The size , and mode of the TList instance are emitted (as
         *             ints), followed by all of its elements (each an Object) in
         *             the proper order.
         */
        private final void writeObject(java.io.ObjectOutputStream s)
                        throws java.io.IOException {
                // Write out element count, and any hidden stuff
                s.defaultWriteObject();

                int expectedModCount = modCount;
                int size = size();
                Leaf nxlf = new Leaf();
                nxlf.setRight(head);

                // Write out list size, mode
                s.writeInt(size);
                s.writeInt(mode);

                // Write out all elements in the proper order.
                for (int i = 0; i < size; i++) {
                        nxlf = nxlf.getRight();
                        s.writeObject(nxlf.getValue());
                }

                if (modCount != expectedModCount) {
                        throw new ConcurrentModificationException();
                }

        }

        /**
         * Reconstitute the TList instance from a stream (that is, deserialize it).
         * 
         * @param s
         *            The java.io.ObjectInputStream from which the TList is to be
         *            reconstituted.
         */
        private final void readObject(java.io.ObjectInputStream s)
                        throws java.io.IOException, ClassNotFoundException {
                // Read in size, and any hidden stuff
                s.defaultReadObject();

                // Read in array length and allocate array
                int size = s.readInt();
                int mode = s.readInt();

                Leaf pv = null, v = null;
                Leaf head = null;

                // Read in all elements in the proper order.
                for (int i = 0; i < size; i++) {
                        pv = v;
                        v = new Leaf(s.readObject());
                        if (pv == null)
                                head = v;
                        else {
                                pv.setRight(v);
                                v.setLeft(pv);
                        }
                }
                TList tlist = getTList(size, head);
                this.root = tlist.root;
                this.head = tlist.head;
                this.tail = tlist.tail;
                this.mode = mode;
                this.uprb = 0;

        }

        /**
         * Optimize the list structure for further use.
         */
        public final void rebuild() {

                class Q { // local work class
                        Node val = null;

                        Q next = null;
                } // end Q

                if (size() < 4)
                        return;

                uprb = 0;

                Q front = new Q();
                front.val = (Node) root;
                Q back = front;
                Q curr = front;
                Object c = null;
                Q q = null;

                while (curr != null) {
                        c = (curr.val).getLeft();
                        if (c instanceof Node) {
                                q = new Q();
                                q.val = (Node) c;
                                (q.val).setWeight(2); // pre-set to min wt.
                                back.next = q;
                                back = q;
                        }
                        c = (curr.val).getRight();
                        if (c instanceof Node) {
                                q = new Q();
                                q.val = (Node) c;
                                (q.val).setWeight(2); // pre-set to min wt.
                                back.next = q;
                                back = q;
                        }
                        //  (curr.val).setLeft(null);
                        //  (curr.val).setRight(null);
                        curr = curr.next;
                } // endwhile

                curr = front;
                Q posn = curr;
                int wt = ((Node) root).getWeight(), wl, wr;

                set_weights: while (wt > 3) {
                        wl = wt / 2;
                        wr = wt - wl;

                        posn = posn.next;
                        (posn.val).setWeight(wl);

                        posn = posn.next;
                        (posn.val).setWeight(wr);

                        curr = curr.next;
                        wt = (curr.val).getWeight();
                } // end set_weights

                curr = front;
                posn = front;
                Node g = null;

                set_children: while (curr != null) {

                        g = curr.val; // never null!
                        wt = g.getWeight();
                        wl = wt / 2;
                        wr = wt - wl;

                        if (wl > 1) {
                                posn = posn.next;
                                g.setLeft(posn.val);
                        }

                        if (wr > 1) {
                                posn = posn.next;
                                g.setRight(posn.val);
                        }

                        curr = curr.next;
                } // end set_children

                int w = size();

                Object node = null, xnode = null;
                Leaf nxlf = new Leaf();
                nxlf.setRight(head);
                int index = 0, first, last, pivot, wn;

                set_leaves: do {

                        node = (Node) root;
                        wn = w;
                        first = 0;
                        last = w;

                        while (wn > 4) {

                                pivot = first;
                                xnode = ((Node) node).getLeft();
                                wn = ((Node) xnode).getWeight();
                                pivot += wn;

                                if (index < pivot) {
                                        last = pivot;
                                        node = xnode;
                                } else {
                                        first = pivot;
                                        node = (Node) (((Node) node).getRight());
                                        wn = ((Node) node).getWeight();
                                }
                        } // end while (wn>4)

                        if (wn == 4) {
                                xnode = (Node) (((Node) node).getLeft());
                                nxlf = nxlf.getRight();
                                ((Node) xnode).setLeft(nxlf);
                                index++;
                                nxlf = nxlf.getRight();
                                ((Node) xnode).setRight(nxlf);
                                index++;
                                node = (Node) (((Node) node).getRight());
                        }
                        if (wn == 3) {
                                nxlf = nxlf.getRight();
                                ((Node) node).setLeft(nxlf);
                                index++;
                                node = (Node) (((Node) node).getRight());
                        }
                        nxlf = nxlf.getRight();
                        ((Node) node).setLeft(nxlf);
                        index++;
                        nxlf = nxlf.getRight();
                        ((Node) node).setRight(nxlf);
                        index++;

                } while (index < w);

        } // end rebuild

        /**
         * rebuild heuristic
         */
        private final void rebuildTest() {

                if (size() < 4)
                        return;

                if (mode == RANDOM)
                        return;
                /*
                 * if ((((Node) root).getLeft() instanceof Leaf) || (((Node)
                 * root).getRight() instanceof Leaf)) { rebuild(); return; }
                 * 
                 * int lw = ((Node) (((Node) root).getLeft())).getWeight(); int rw =
                 * ((Node) (((Node) root).getRight())).getWeight(); if (lw > (rw + (rw /
                 * 10)) || rw > (lw + (lw / 10))) { rebuild(); return; }
                 */
                int exp, v;
                /*
                 * int z = size(); exp = 1; v = 1;
                 * 
                 * getlog: while (exp < 32) { v += v; // i.e. v = 2**exp if (v < z)
                 * exp++; else break getlog; }
                 */

                for (exp = 0, v = size(); v > 0; exp++, v /= 2)
                        continue;

                if (uprb < 1024 * exp)
                        return;
                else
                        rebuild();

        } // end rebuildTest

        private class Node {

                private int weight; // weight of Node

                private Object left, right; // left, right children

                Node() {
                        weight = 0;
                        left = null;
                        right = null;
                }

                Node(int aWeight) {
                        weight = aWeight;
                        left = null;
                        right = null;
                }

                final void setWeight(int aWeight) {
                        weight = aWeight;
                }

                final void setLeft(Object lchild) {
                        left = lchild;
                }

                final void setRight(Object rchild) {
                        right = rchild;
                }

                final int getWeight() {
                        return weight;
                }

                final Object getLeft() {
                        return left;
                }

                final Object getRight() {
                        return right;
                }
        } // end of Node class

        private class Leaf {

                private Leaf llink, rlink; // left, right links

                private Object value; // value in Leaf

                Leaf() {
                        llink = null;
                        rlink = null;
                        value = null;
                }

                Leaf(Object o) {
                        llink = null;
                        rlink = null;
                        value = o;
                }

                final void setValue(Object o) {
                        value = o;
                }

                final Object getValue() {
                        return value;
                }

                final void setLeft(Leaf lleaf) {
                        llink = lleaf;
                }

                final void setRight(Leaf rleaf) {
                        rlink = rleaf;
                }

                final Leaf getLeft() {
                        return llink;
                }

                final Leaf getRight() {
                        return rlink;
                }

        } // end of Leaf class

        private class SubTList extends AbstractList implements List, RandomAccess {

                private AbstractList list;

                private int offset;

                private int size;

                private Leaf head;

                private Leaf tail;

                private int expectedModCount;

                /**
                 *  
                 */
                public void add(int index, Object o) throws IndexOutOfBoundsException {
                        if (index < 0)
                                throw new IndexOutOfBoundsException();
                        if (index > size)
                                throw new IndexOutOfBoundsException();

                        if (list instanceof SubTList) {
                                if (((SubTList) list).modCount != expectedModCount)
                                        throw new ConcurrentModificationException();
                        } else if (TList.this.modCount != expectedModCount)
                                throw new ConcurrentModificationException();

                        // list.add(index + offset, o);
                        try {
                                if (list instanceof SubTList) {
                                        ((SubTList) list).add(index + offset, o);
                                        expectedModCount = ((SubTList) list).modCount;
                                } else {
                                        TList.this.add(index + offset, o);
                                        expectedModCount = TList.this.modCount;
                                }
                                size++;
                                modCount++;
                                head = getLeaf(0);
                                tail = getLeaf(size - 1);
                        } catch (IndexOutOfBoundsException e) {
                                throw new ConcurrentModificationException();
                        }
                }

                /**
                 * 
                 * @param o
                 * @return true
                 */
                public boolean add(Object o) {
                        add(size, o);
                        return true;
                }

                /**
                 * 
                 * @param o
                 */
                private void addFirst(Object o) {
                        add(0, o);
                }

                /**
                 * 
                 * @param o
                 */
                private void addLast(Object o) {
                        add(size, o);
                }

                /**
                 * 
                 * @param c
                 * @return true
                 */
                public boolean addAll(Collection c) {
                        return addAll(size, c);
                }

                /**
                 *  
                 */
                public boolean addAll(int index, Collection c)
                                throws IndexOutOfBoundsException {
                        if (index < 0)
                                throw new IndexOutOfBoundsException();
                        if (index > size)
                                throw new IndexOutOfBoundsException();
                        int cize = c.size();
                        if (cize == 0)
                                return false;

                        if (list instanceof SubTList) {
                                if (((SubTList) list).modCount != expectedModCount)
                                        throw new ConcurrentModificationException();
                        } else if (TList.this.modCount != expectedModCount)
                                throw new ConcurrentModificationException();

                        if (list instanceof SubTList) {
                                ((SubTList) list).addAll(index + offset, c);
                                expectedModCount = ((SubTList) list).modCount;
                        } else {
                                TList.this.addAll(index + offset, c);
                                expectedModCount = TList.this.modCount;
                        }

                        size += cize;
                        modCount++;
                        return true;
                }

                /**
                 *  
                 */
                protected void removeRange(int fromIndex, int toIndex)
                                throws IndexOutOfBoundsException, IllegalArgumentException {
                        if (fromIndex < 0 || toIndex > size)
                                throw new IndexOutOfBoundsException();

                        if (fromIndex > toIndex)
                                throw new IllegalArgumentException();

                        if (fromIndex == toIndex)
                                return;

                        if (list instanceof SubTList) {
                                if (((SubTList) list).modCount != expectedModCount)
                                        throw new ConcurrentModificationException();
                        } else if (TList.this.modCount != expectedModCount)
                                throw new ConcurrentModificationException();

                        if (list instanceof SubTList) {
                                ((SubTList) list).removeRange(fromIndex + offset, toIndex
                                                + offset);
                                expectedModCount = ((SubTList) list).modCount;
                        } else {
                                TList.this.removeRange(fromIndex + offset, toIndex + offset);
                                expectedModCount = TList.this.modCount;
                        }

                        size -= (toIndex - fromIndex);
                        modCount++;
                        if (size == 0) {
                                head = null;
                                tail = null;
                        } else {
                                head = getLeaf(0);
                                tail = getLeaf(size - 1);
                        }
                }

                /**
                 *  
                 */
                public void clear() {
                        int fromIndex = 0;
                        int toIndex = size;
                        removeRange(fromIndex, toIndex);
                }

                /**
                 *  
                 */
                public boolean contains(Object o) {

                        Iterator iter = iterator();
                        while (iter.hasNext()) {
                                Object o2 = iter.next();
                                if (o == null) {
                                        if (o2 == null)
                                                return true;
                                } else { // o != null
                                        if (o2 != null)
                                                if (o2.equals(o))
                                                        return true;
                                }
                        }
                        return false;
                }

                /*
                 *  
                 */
                public boolean containsAll(Collection c) throws NullPointerException {

                        if (c == null)
                                throw new NullPointerException();
                        Iterator iter = c.iterator();
                        while (iter.hasNext()) {
                                if (!this.contains(iter.next()))
                                        return false;
                        }
                        return true;
                }

                /*
                 *  
                 */
                public Object get(int index) throws IndexOutOfBoundsException {

                        if (index < 0 || index >= size)
                                throw new IndexOutOfBoundsException();

                        if (list instanceof SubTList) {
                                if (((SubTList) list).modCount != expectedModCount)
                                        throw new ConcurrentModificationException();
                        } else if (TList.this.modCount != expectedModCount)
                                throw new ConcurrentModificationException();

                        if (list instanceof SubTList)
                                return ((SubTList) list).get(index + offset);
                        else
                                return TList.this.get(index + offset);
                }

                private Leaf getLeaf(int index) throws IndexOutOfBoundsException {

                        if (index < 0 || index >= size)
                                throw new IndexOutOfBoundsException();

                        if (list instanceof SubTList) {
                                if (((SubTList) list).modCount != expectedModCount)
                                        throw new ConcurrentModificationException();
                        } else if (TList.this.modCount != expectedModCount)
                                throw new ConcurrentModificationException();

                        if (list instanceof SubTList)
                                return ((SubTList) list).getLeaf(index + offset);
                        else
                                return TList.this.getLeaf(index + offset);
                }

                private Object getFirst() {
                        if (root == null)
                                throw new NoSuchElementException();

                        return get(0);
                }

                private Object getLast() {
                        if (root == null)
                                throw new NoSuchElementException();

                        return get(size - 1);
                }

                /*
                 *  
                 */
                public int indexOf(Object o) {
                        ListIterator lter = listIterator();
                        if (o == null) {
                                while (lter.hasNext())
                                        if (lter.next() == null)
                                                return lter.previousIndex();
                        } else {
                                while (lter.hasNext())
                                        if (o.equals(lter.next()))
                                                return lter.previousIndex();
                        }
                        return -1;
                }

                /*
                 *  
                 */
                public boolean isEmpty() {
                        return size == 0;
                }

                /*
                 *  
                 */
                public Iterator iterator() {

                        final SubTList sublist = this;

                        return new Iterator() {

                                Leaf curr;

                                int index;

                                boolean ncall;

                                int expectedModCount;

                                {
                                        if (size() == 0)
                                                curr = null;
                                        else
                                                curr = head;
                                        index = 0;
                                        ncall = false;
                                        expectedModCount = modCount;
                                }

                                public final void remove() throws IllegalStateException {

                                        if (ncall == false)
                                                throw new IllegalStateException();
                                        if (modCount != expectedModCount)
                                                throw new ConcurrentModificationException();
                                        try {
                                                ncall = false;
                                                sublist.remove(--index);
                                                expectedModCount = modCount;
                                                //      if (index == size)
                                                //              curr = null;
                                                //      else
                                                //              curr = getLeaf(index);
                                        } catch (IndexOutOfBoundsException e) {
                                                throw new ConcurrentModificationException();
                                        }
                                }

                                public final boolean hasNext() {
                                        return curr != null;
                                }

                                public final Object next() throws NoSuchElementException {

                                        /*
                                         * if (index == size) throw new NoSuchElementException();
                                         */
                                        if (modCount != expectedModCount)
                                                throw new ConcurrentModificationException();
                                        try {
                                                // Object v = get(index++);
                                                Object v = curr.getValue();
                                                index++;
                                                if (index == size)
                                                        curr = null;
                                                else
                                                        curr = curr.getRight();
                                                ncall = true;
                                                return v;
                                        } catch (IndexOutOfBoundsException e) {
                                                if (modCount != expectedModCount)
                                                        throw new ConcurrentModificationException();
                                                throw new NoSuchElementException();
                                        }
                                }
                        };
                }

                /*
                 *  
                 */
                public int lastIndexOf(Object o) {
                        ListIterator lter = listIterator(size());
                        if (o == null) {
                                while (lter.hasPrevious())
                                        if (lter.previous() == null)
                                                return lter.nextIndex();
                        } else {
                                while (lter.hasPrevious())
                                        if (o.equals(lter.previous()))
                                                return lter.nextIndex();
                        }
                        return -1;
                }

                /*
                 *  
                 */
                public ListIterator listIterator() {
                        return listIterator(0);
                } // end of listIterator method

                /**
                 *  
                 */
                public ListIterator listIterator(final int position)
                                throws IndexOutOfBoundsException {

                        final SubTList sublist = this;

                        if (position < 0 || position > size)
                                throw new IndexOutOfBoundsException();

                        return new ListIterator() {

                                Leaf curr;

                                Leaf prev;

                                int index;

                                boolean ncall;

                                boolean pcall;

                                boolean acall;

                                boolean rcall;

                                int expectedModCount;

                                {
                                        index = position;
                                        if (index == size)
                                                curr = null;
                                        else
                                                curr = getLeaf(index);
                                        if (index == 0)
                                                prev = null;
                                        else
                                                prev = getLeaf(index - 1);
                                        ncall = false;
                                        pcall = false;
                                        acall = false;
                                        rcall = false;
                                        expectedModCount = modCount;
                                }

                                public final boolean hasPrevious() {
                                        return prev != null;
                                }

                                public final Object next() throws NoSuchElementException {

                                        /*
                                         * if (index == size) throw new NoSuchElementException();
                                         */
                                        if (modCount != expectedModCount)
                                                throw new ConcurrentModificationException();
                                        try {
                                                // Object v = get(index++);
                                                Object v = curr.getValue();
                                                prev = curr;
                                                index++;
                                                if (index == size) {
                                                        curr = null;
                                                        //      prev = tail;
                                                } else {
                                                        //      prev = curr;
                                                        curr = prev.getRight();
                                                }
                                                ncall = true;
                                                pcall = false;
                                                acall = false;
                                                rcall = false;
                                                return v;
                                        } catch (IndexOutOfBoundsException e) {
                                                if (modCount != expectedModCount)
                                                        throw new ConcurrentModificationException();
                                                throw new NoSuchElementException();
                                        }
                                }

                                public final int nextIndex() {
                                        return index;
                                }

                                public final Object previous() throws NoSuchElementException {

                                        /*
                                         * if (index == 0) throw new NoSuchElementException();
                                         */
                                        if (modCount != expectedModCount)
                                                throw new ConcurrentModificationException();
                                        try {
                                                // Object v = get(--index);
                                                --index;
                                                Object v = prev.getValue();
                                                curr = prev;
                                                if (index == 0) {
                                                        prev = null;
                                                        //      curr = head;
                                                } else {
                                                        //      curr = prev;
                                                        prev = curr.getLeft();
                                                }
                                                pcall = true;
                                                ncall = false;
                                                acall = false;
                                                rcall = false;
                                                return v;
                                        } catch (IndexOutOfBoundsException e) {
                                                if (modCount != expectedModCount)
                                                        throw new ConcurrentModificationException();
                                                throw new NoSuchElementException();
                                        }
                                }

                                public final int previousIndex() {
                                        return index - 1;
                                }

                                public final void remove() throws IllegalStateException {

                                        if ((ncall == false) && (pcall == false))
                                                throw new IllegalStateException();
                                        if (modCount != expectedModCount)
                                                throw new ConcurrentModificationException();
                                        try {
                                                /*
                                                 * if (ncall) index--; sublist.remove(index);
                                                 * expectedModCount = modCount; if (index == 0) prev =
                                                 * null; else prev = getLeaf(index - 1); if (index ==
                                                 * size) curr = null; else curr = getLeaf(index);
                                                 */
                                                if (ncall) {
                                                        sublist.remove(--index); // prev
                                                        expectedModCount = modCount;
                                                        if (index == 0)
                                                                prev = null;
                                                        else
                                                                prev = prev.getLeft();
                                                } else { // pcall
                                                        sublist.remove(index); // curr
                                                        expectedModCount = modCount;
                                                        if (index == size)
                                                                curr = null;
                                                        else
                                                                curr = curr.getRight();
                                                }
                                                rcall = true;
                                                ncall = false;
                                                pcall = false;
                                                acall = false;
                                        } catch (IndexOutOfBoundsException e) {
                                                throw new ConcurrentModificationException();
                                        }
                                }

                                public final boolean hasNext() {
                                        return curr != null;
                                }

                                public final void add(Object o) {
                                        if (modCount != expectedModCount)
                                                throw new ConcurrentModificationException();
                                        try {
                                                /*
                                                 * sublist.add(index, o); expectedModCount = modCount;
                                                 * prev = getLeaf(index++); if (index == size()) { curr =
                                                 * null; prev = tail; } else curr = getLeaf(index);
                                                 */
                                                sublist.add(index, o); // increments modCount
                                                expectedModCount = modCount;
                                                index++;
                                                // if ((curr == null) || (size == 1))
                                                if (curr == null)
                                                        prev = sublist.tail;
                                                else {
                                                        curr = sublist.getLeaf(index);
                                                        prev = curr.getLeft();
                                                }
                                                acall = true;
                                                ncall = false;
                                                pcall = false;
                                                rcall = false;
                                        } catch (IndexOutOfBoundsException e) {
                                                throw new ConcurrentModificationException();
                                        }
                                }

                                public final void set(Object o) throws IllegalStateException {

                                        if ((ncall == false) && (pcall == false))
                                                throw new IllegalStateException();
                                        if (modCount != expectedModCount)
                                                throw new ConcurrentModificationException();
                                        if (ncall)
                                                // sublist.set(index - 1, o);
                                                prev.setValue(o);
                                        else
                                                // sublist.set(index, o);
                                                curr.setValue(o);
                                } // end set

                        }; // end of returned anonymous class

                } // end of listIterator method

                /**
                 *  
                 */
                public Object remove(int index) throws IndexOutOfBoundsException {

                        if (index < 0)
                                throw new IndexOutOfBoundsException();
                        if (index >= size)
                                throw new IndexOutOfBoundsException();

                        if (list instanceof SubTList) {
                                if (((SubTList) list).modCount != expectedModCount)
                                        throw new ConcurrentModificationException();
                        } else if (TList.this.modCount != expectedModCount)
                                throw new ConcurrentModificationException();

                        try {
                                Object o = null;
                                if (list instanceof SubTList) {
                                        o = ((SubTList) list).remove(index + offset);
                                        expectedModCount = ((SubTList) list).modCount;
                                } else {
                                        o = TList.this.remove(index + offset);
                                        expectedModCount = TList.this.modCount;
                                }
                                size--;
                                modCount++;

                                if (size == 0) {
                                        head = null;
                                        tail = null;
                                } else {
                                        head = getLeaf(0);
                                        tail = getLeaf(size - 1);
                                }
                                return o;
                        } catch (IndexOutOfBoundsException e) {
                                throw new ConcurrentModificationException();
                        }
                }

                private Object removeFirst() {
                        return remove(0);
                }

                private Object removeLast() {
                        return remove(size - 1);
                }

                /*
                 *  
                 */
                public boolean remove(Object o) {
                        Iterator iter = this.iterator();
                        boolean changed = false;
                        Object o2 = null;
                        while (iter.hasNext()) {
                                o2 = iter.next();
                                if (o == null) {
                                        if (o2 == null) {
                                                iter.remove();
                                                // changed = true;
                                                return true;
                                        }
                                } else { // o != null
                                        if (o2 != null)
                                                if (o2.equals(o)) {
                                                        iter.remove();
                                                        // changed = true;
                                                        return true;
                                                }
                                }
                        }
                        // return changed;
                        return false;
                }

                /**
                 *  
                 */
                public boolean removeAll(Collection c) throws NullPointerException {

                        if (c == null)
                                throw new NullPointerException();

                        boolean changed = false;
                        Iterator iter = this.iterator();
                        while (iter.hasNext()) {
                                if (c.contains(iter.next())) {
                                        iter.remove();
                                        changed = true;
                                }
                        }
                        return changed;
                }

                /**
                 *  
                 */
                public boolean retainAll(Collection c) throws NullPointerException {
                        if (c == null)
                                throw new NullPointerException();
                        boolean changed = false;
                        Iterator iter = this.iterator();
                        while (iter.hasNext()) {
                                if (!c.contains(iter.next())) {
                                        iter.remove();
                                        changed = true;
                                }
                        }
                        return changed;
                }

                /*
                 *  
                 */
                public Object set(int index, Object o) throws IndexOutOfBoundsException {

                        if (index < 0 || index >= size)
                                throw new IndexOutOfBoundsException();

                        if (list instanceof SubTList) {
                                if (((SubTList) list).modCount != expectedModCount)
                                        throw new ConcurrentModificationException();
                        } else if (TList.this.modCount != expectedModCount)
                                throw new ConcurrentModificationException();
                        try {
                                Object v = null;
                                if (list instanceof SubTList) {
                                        v = ((SubTList) list).set(index + offset, o);
                                        //      expectedModCount = ((SubTList) list).modCount;
                                } else {
                                        v = TList.this.set(index + offset, o);
                                        //      expectedModCount = TList.this.modCount;
                                }
                                // modCount++;
                                return v;
                        } catch (IndexOutOfBoundsException e) {
                                throw new ConcurrentModificationException();
                        }
                }

                /*
                 *  
                 */
                public int size() {

                        if (list instanceof SubTList) {
                                if (((SubTList) list).modCount != expectedModCount)
                                        throw new ConcurrentModificationException();
                        } else if (TList.this.modCount != expectedModCount)
                                throw new ConcurrentModificationException();

                        return size;
                }

                /**
                 *  
                 */
                public List subList(int fromIndex, int toIndex) {
                        return new SubTList(this, fromIndex, toIndex);
                }

                /**
                 *  
                 */
                public Object[] toArray() {
                        int s = size;
                        Object[] o = new Object[s];
                        Iterator iter = iterator();
                        int i = 0;
                        while (iter.hasNext()) {
                                o[i] = iter.next();
                                i++;
                        }
                        return o;
                }

                /**
                 *  
                 */
                public Object[] toArray(Object[] o) throws NullPointerException {
                        if (o == null)
                                throw new NullPointerException();
                        int s = size;
                        if (o.length < s)
                                o = (Object[]) java.lang.reflect.Array.newInstance(o.getClass()
                                                .getComponentType(), s);
                        Iterator iter = iterator();
                        int i = 0;
                        while (iter.hasNext()) {
                                o[i] = iter.next();
                                i++;
                        }
                        if (o.length > s)
                                o[s] = null;
                        return o;
                }

                /**
                 *  
                 */
                public SubTList() {
                }

                /**
                 * Constructs a sublist over this list starting at location fromIndex
                 * and ending at toIndex-1. If fromIndex=toIndex the sublist is empty.
                 * 
                 * @param aList
                 *            The base List over which the sublist is defined.
                 * @param fromIndex
                 *            The base index where the sublist begins.
                 * @param toIndex
                 *            The base index which is 1 past the end of the sublist.
                 * @throws IndexOutOfBoundsException
                 *             if fromIndex is less than 0 or toIndex is greater than
                 *             aList.size().
                 * @throws IllegalArgumentException
                 *             if fromIndex is greater than toIndex.
                 */
                public SubTList(AbstractList aList, int fromIndex, int toIndex)
                                throws IndexOutOfBoundsException, IllegalArgumentException {

                        list = aList;
                        int sz = 0;
                        if (list instanceof SubTList)
                                sz = ((SubTList) list).size;
                        else
                                sz = TList.this.size();
                        if (fromIndex < 0)
                                throw new IndexOutOfBoundsException();
                        if (toIndex > sz)
                                throw new IndexOutOfBoundsException();
                        if (fromIndex > toIndex)
                                throw new IllegalArgumentException();

                        size = toIndex - fromIndex;
                        offset = fromIndex;
                        if (list instanceof SubTList)
                                expectedModCount = ((SubTList) list).modCount;
                        else
                                expectedModCount = TList.this.modCount;

                        if (size == 0) {
                                head = null;
                                tail = null;
                        } else {
                                head = getLeaf(0);
                                if (size == 1)
                                        tail = head;
                                else
                                        tail = getLeaf(size - 1);
                        }
                }

        } // end of subTList

} // end of TList class
