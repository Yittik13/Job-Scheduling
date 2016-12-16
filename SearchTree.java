/*Search Tree class
 * Your implementation goes in this file
 *
 * @author Joel Uban
 * @login juban
 * @date November , 2016
 * @pso 17
 * @collaborators Jun Soo Kim, Elvin Uthuppan
 */

import java.util.*;

public class SearchTree {

    private Node root; //The root of the RB Tree
    //public Node root; //CHANGE BACK
    private JobTable jobs; //The jobTable
    //The following variables are used for measuring utilities, do not change them!
    private int[] machineLoads;
    private int numOfMachine;
    private int scheduled;
    private int requests;
    //You can add any other variables here if needed

    //Create a balanced search tree consisting of all the machines initially empty
    public SearchTree(int space, int numOfMachine) {
        jobs = new JobTable();
        scheduled = requests = 0;
        this.numOfMachine = numOfMachine;
        machineLoads = new int[numOfMachine];
        machineLoads[0] = 0;
        root = new Node(0, space, 0);
        for (int i = 1; i < numOfMachine; i++) {
            insertNewNode(i, space, 0);
            machineLoads[i] = 0;
        }
        //System.out.println("ROOT: " + root);
    }

    public SearchTree() {}

    public SearchTree(JobTable jt) {
        jobs = jt;
    }

    public void insertNewNode(int machine, int free, int numjobs) {
        root = RedBlackBST.insert(root, new Node(machine, free, numjobs));
    }

    //TODO: All parts needed for your implementation are in functions listed below:

    //Find the machine with just enough free space to schedule a job
    //Update the free size and number of jobs on the machine
    //Return machine id... -1 if no such machine exists
    public int scheduleJobMinSpace(int jobid, int size) {
        Node m;
        requests++;
        /* Do not modify the code above */

		/* TODO: Start your implementation here, find m to schedule */

        //System.out.println("scheduling " + jobid + " ...");// size: " + size);

        if (count(size) == 0) {
            //System.out.println("FAILED");
            return -1;
        }

        if (root == null) {
            return -1;
        }

        m = searchMS(null, root, size);

        //System.out.println("Success! Scheduled to " + m.id);

        Node temp = new Node(m.id, m.free, m.numjobs);

        root = RedBlackBST.delete(root, m);

        m = new Node(temp.id, temp.free, temp.numjobs);
        m.addJob(size);

        root = RedBlackBST.insert(root, m);

        temp = get(root, m);

        jobs.addJob(jobid, size, temp);

        //System.out.println("----------------------------------");
        //inOrder(root);
        //System.out.println("----------------------------------");

		/* Do not modify the following part */
        scheduled++;
        machineLoads[m.id]++;
        return m.id;
    }

    /*
     * Recursive method used to find the optimal node according to the min space strategy
     * @param best: the current best node found so far
     * @param current: node used to traverse the tree
     * @param sizeReq: the size of the job to schedule
     */
    private Node searchMS(Node best, Node current, int sizeReq) {
        if (current == null) {
            return best;
        }

        else if (current.free < sizeReq) { //not enough space, look right
            return searchMS(best, current.right, sizeReq);
        }

        else { //enough space
            if (best == null) { //best has not been found yet
                if (current.free == sizeReq) {
                    best = current;
                    return searchMS(best, current.left, sizeReq);

                }
                else if (current.free > sizeReq) {
                    best = current;
                    return searchMS(best, current.left, sizeReq);
                }
                else {
                    return searchMS(null, current.right, sizeReq);
                }
            }
            else { //best has been found before
                if (best.free == current.free) {
                    return searchMS(best, current.left, sizeReq);
                }
                else if (best.free > current.free) {
                    best = current;
                    return searchMS(best, current.left, sizeReq);
                }
                else {
                    return searchMS(best, current.right, sizeReq);
                }
            }
        }
    }


    //Find the machine with enough free space and minimum number of jobs to schedule a job
    //Update the free size and number of jobs on the machine
    //Return machine id... -1 if no such machine exists
    public int scheduleJobMinJob(int jobid, int size) {

        Node m; // CHANGE BACK TO Node m;
        requests++;
		/* TODO: Start your implementation here: Find node m to schedule the job  */

        //System.out.println("Scheduleing " + jobid + " ...");

        if (count(size) == 0) {
            return -1;
        }

        if (root == null) {
            return -1;
        }

        m = searchMJ(null, root, size);

        if (m == null) {
            return -1;
        }

        //System.out.println("Success! Scheduled to "  + m.id);

        Node temp = new Node(m.id, m.free, m.numjobs);

        root = RedBlackBST.delete(root, m);

        m = new Node(temp.id, temp.free, temp.numjobs);
        m.addJob(size);

        root = RedBlackBST.insert(root, m);

        temp = get(root, m);

        jobs.addJob(jobid, size, temp);

        //System.out.println("----------------------------------");
        //inOrder(root);
        //System.out.println("----------------------------------");

		/* Do not modify the following part */
        machineLoads[m.id]++;
        scheduled++;
        return m.id;
    }


    /*
     * Recursive method used to find the optimal node according to the min job strategy
     * @param best: the current best node found so far
     * @param current: node used to traverse the tree
     * @param sizeReq: the size of the job to schedule
     */
    private Node searchMJ(Node best, Node current, int spaceReq) {
        Node w;
        if (current == null) { //base case
            return best;
        }

        else if (current.free < spaceReq) { //not enough space, look right
            return searchMJ(best, current.right, spaceReq);
        }

        else { //node has enough space, look left to see if there's a better option
            /* if current has more jobs than minJobsNode of its right subtree */
            if (current.right != null) { //make sure current has a right child
                /* determine w */
                if (current.numjobs > current.right.minJobsNode.numjobs) {
                    w = current.right.minJobsNode;
                }
                else if (current.numjobs == current.right.minJobsNode.numjobs) {
                    if (current.free > current.right.minJobsNode.free) {
                        w = current.right.minJobsNode;
                    }
                    else if (current.free == current.right.minJobsNode.free) {
                        if (current.id > current.right.minJobsNode.id) {
                            w = current.right.minJobsNode;
                        }
                        else {
                            w = current;
                        }
                    }
                    else {
                        w = current;
                    }
                }
                else {
                    w = current;
                }

                if (w.free >= spaceReq) { //check if w even has enough space
                    if (best == null) {
                        best = w;
                        return searchMJ(best, current.left, spaceReq);
                    }

                    if (w.numjobs < best.numjobs) { //check if w has less jobs
                        best = w; //if so update best
                        return searchMJ(best, current.left, spaceReq);
                    }
                    else if (w.numjobs == best.numjobs) { //if same number of jobs
                        if (w.free < best.free) { //check if w has less space
                            best = w; //if so, update best
                            return searchMJ(best, current.left, spaceReq);
                        }
                        else if (w.free == best.free) { //if same amount of free space
                            if (w.id < best.id) { //check which id is smaller
                                best = w; //if w has smaller id, update best
                                return searchMJ(best, current.left, spaceReq);
                            }
                            else { //w.id > best.id
                                return searchMJ(best, current.left, spaceReq);
                            }
                        }
                        else { //w.free > best.free
                            return searchMJ(best, current.left, spaceReq);
                        }
                    }
                    else { //w.numJobs > best.numJobs
                        return searchMJ(best, current.left, spaceReq);
                    }
                }
                else { //w.free < spaceReq: w does not have enough space for job
                    return searchMJ(best, current.right, spaceReq);
                }
            }
            else { //current.right == null, look left
                if (best == null) {
                    best = current;
                    return searchMJ(best, current.left, spaceReq);
                }
                /* CHECK IF CURRENT IS BETTER THAN BEST AND UPDATE BEST IF SO */
                if (current.free >= spaceReq) { //make sure current actually has enough space
                    if (current.numjobs < best.numjobs) { //if current has less jobs
                        best = current; //update best
                    } else if (current.numjobs == best.numjobs) { //same number of jobs
                        if (current.free < best.free) { //if current has less free space
                            best = current; //update best
                        } else if (current.free == best.free) { //if current and best have same free space
                            if (current.id < best.id) { //if current has smaller machine id
                                best = current; //update best
                            }
                        }
                    }
                }
                return searchMJ(best, current.left, spaceReq); //continue search left
            }
        }
    }


    //Update the free space and number of jobs on machine releasing a job
    public void releaseJob(int jobid) {

        Node m = jobs.jobMachine(jobid);

        /* TODO: Release m */

        if (m != null) {

            //System.out.println("Release job " + jobid + " of size " + jobs.jobSize(jobid) + " from: " + m);

            Node temp = new Node(m.id, m.free, m.numjobs); //make a temp node (deep copy)

            int size = jobs.jobSize(jobid); //find the size of the job to release

            root = RedBlackBST.delete(root, m); //delete node from tree

            m = new Node(temp.id, temp.free, temp.numjobs); //reassign m to the copy we made

            m.removeJob(size); //remove the job

            root = RedBlackBST.insert(root, m); //reinsert new node back into tree

            Node t = get(root, m);

            jobs.deleteJob(jobid, t); //remove job from job table

            //System.out.println("UPDATED MACHINE: " + t);

            machineLoads[m.id]--;

        }
        else {
            //System.out.println("NO JOB TO RELEASE");
        }
    }

    /*
     * Added Helper Function: Find and return the specified node in the tree
     * Used in order to determine the children of a node after we insert it into the tree
     */
    public static Node get(Node root, Node find) {
        Node x = root;
        while (x != null) {
            if (find.free < x.free) {
                x = x.left;
            }
            else if (find.free > x.free) {
                x = x.right;
            }
            else {
                /* have to make sure the correct node is found (ids match) */
                if (find.id == x.id) {
                    return x;
                }
                else {
                    if (find.id > x.id) {
                        x = x.right;
                    }
                    else {
                        x = x.left;
                    }
                }
            }
        }
        return null;
    }

    //Return the number of machines that have at least given free space
    //USE RECURSION
    public int count(int free) {
		
		/* TODO: start your implementation here */

		return count(free, root);
    }
    private int count(int free, Node x) { //recursive helper method for count
        if (x == null) {
            return 0;
        }

        if (free <= x.free) {
            return 1 + size(x.right) + count(free, x.left);
        }
        else {
            return count(free, x.right);
        }
    }

    //return the size of a node if it exists. Helper function
    private int size(Node x) { //helper method
        if (x == null) return 0;
        return x.size;
    }

    /*
    * DO NOT EDIT THE FOLLOWING FUNCTION
    * IT IS INVOLVED IN MEASURING THE UTILITIES FOR EXPERIMENTAL SECTION
    */
    public void measureUtility() {
        double ideal = 0.0;
        double medianload = 0.0;
        ArrayList<Integer> loads = new ArrayList<Integer>();
        int size = 0; //total combined size of all jobs
        for (int i = 0; i < numOfMachine; i++) {
            int load = machineLoads[i];
            loads.add(load);
            size += load;
        }

        int len = loads.size();

        Collections.sort(loads);
        if (size % 2 == 0) {
            medianload = loads.get(len / 2);
        } else {
            medianload = (loads.get(len / 2) + loads.get(len / 2 + 1)) / 2;
        }
        System.out.println(size);
        ideal = size / (double) numOfMachine;
        double fairness = medianload / ideal;
        double thoroughput = scheduled / (double) requests;
        System.out.format("Fairness: %f, Thoroughput: %f\n", fairness, thoroughput);
        //System.out.println("Median Load: " + medianload); //ADDED
        //System.out.println("Ideal: " + ideal); //ADDED
    }
	/*
	* DO NOT EDIT THE FUNCTION ABOVE
	*/

	/* Helper function
	 * Print the tree in ascending order by key value
	 */
    public void inOrder(Node root) {
        if (root != null) {
            inOrder(root.left);
            System.out.println(root);
            //System.out.println("Left: " + root.left);
            //System.out.println("Right: " + root.right);
            inOrder(root.right);
        }
    }
/*
    public static void main(String[] args) {
        //SearchTree(space, number of machines)
        //SearchTree st = new SearchTree();
        SearchTree st = new SearchTree(100, 25);
        //Random random = new Random();


        int i;
        int numMachines = 15;

        for (i = 0; i < numMachines; i++) {
            st.insertNewNode(i, random.nextInt(35) + 5, 0);
        }

        st.inOrder(st.root);

        System.out.println("\nTotal number of machines: " + numMachines + "\n");

        for (i = 5; i <= 40; i++) {
            System.out.printf("Number of machines with at least %d free space: %d\n", i, st.count(i));
        }



        40 total space
        4 machines, 10 space each

        1 10
        2 10
        3 10
        4 10

        1
        2
        3
        4

        1   5
        2   5
        3   5
        4   5
        5   4
        6   4
        7   4
        8   4
        9   2
        10  2



        //System.out.println("Root: " + st.root);
        //st.inOrder(st.root);
        //System.out.println("ID: " + st.scheduleJobMinSpace(1, 20));
        //System.out.println();
        //st.inOrder(st.root);
        //System.out.println();

        st.scheduleJobMinSpace(1, 30);
        st.scheduleJobMinSpace(2, 20);
        st.scheduleJobMinSpace(3, 50);
        st.scheduleJobMinSpace(4, 10);
        st.scheduleJobMinSpace(5, 10);
        st.scheduleJobMinSpace(6, 10);
        st.scheduleJobMinSpace(7, 10);
        st.scheduleJobMinSpace(8, 10);

        //System.out.println("root: " + st.root);
        //System.out.println("RLEFT: " + st.root.left);
        //System.out.println("RRIGHT: " + st.root.right);

        System.out.println();
        st.inOrder(st.root);
        System.out.println("\nRoot: " + st.root);
        System.out.println("root.left: " + st.root.left);
        System.out.println("root.right: " + st.root.right);
        System.out.println("root.left.left: " + st.root.left.left);
        System.out.println("root.left.right: " + st.root.left.right);
        System.out.println("r.l.l.l: " + st.root.left.left.left);
        System.out.println("r.l.l.r: " + st.root.left.left.right);


        System.out.println("ROOT: " + st.root);
        st.scheduleJobMinJob(0, 28);
        //st.inOrder(st.root);
        //System.out.println();

        //st.scheduleJobMinJob(1, 10);
        //st.releaseJob(3);

        //System.out.println();
        //st.inOrder(st.root);
    }
*/
}
