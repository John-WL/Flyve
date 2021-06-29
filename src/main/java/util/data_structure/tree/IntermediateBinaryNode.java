package util.data_structure.tree;

public class IntermediateBinaryNode<T> implements BinaryNode{

    public IntermediateBinaryNode<T> left;
    public IntermediateBinaryNode<T> right;

    public IntermediateBinaryNode(IntermediateBinaryNode<T> left, IntermediateBinaryNode<T> right) {
        this.left = left;
        this.right = right;
    }
}
