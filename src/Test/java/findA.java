import java.util.*;

/**
 * Created by yccao on 2017/8/3.
 */
/*算法思路:使用hashmap存储树,使用HashSet存节点1的所有父节点,遍历节点2的父节点,出现的第一个即为最近公共父节点
* */
public class findA {
    private class TreeNode
    {
        int val;
        TreeNode left;
        TreeNode right;
    }
    public static TreeNode finda(TreeNode root, TreeNode node1, TreeNode node2) {
        Map<TreeNode, TreeNode> parent = new HashMap();
        Queue<TreeNode> queue = new LinkedList();
        parent.put(root, null);
        queue.add(root);
        while (!parent.containsKey(node1) || !parent.containsKey(node2)) {
            TreeNode node = queue.poll();
            if (node != null) {
                parent.put(node.left, node);
                parent.put(node.right, node);
                queue.add(node.left);
                queue.add(node.right);
            }
        }
        Set<TreeNode> set = new HashSet();//1节点所有
        while (node1 != null) {
            set.add(node1);
            node1 = parent.get(node1);
        }
        while (!set.contains(node2)) {//第一个出现的节点，就是return
            node2 = parent.get(node2);
        }
        return node2;
    }

}
