package com.System_Z_Modulami;

import java.util.List;
import java.util.Random;

public class Node2 {
	int attribute; // can be string, whatever. just something that tells wich attribute this node
					// checks.
	float weight; // i assume this will be random
	Node2[] children = new Node2[2]; // only 2 child cause we need a yes/no answer at each node
	boolean is_leaf;

	public void buildTree(List<Integer> attributes, int depth) {
		if (depth < attributes.size()) {
			this.attribute = attributes.get(depth);
			Random r = new Random();
			this.weight = r.nextInt(2); // 0 or 1
			for (int i = 0; i < this.children.length; ++i) {
				this.children[i] = new Node2();
				this.children[i].buildTree(attributes, depth + 1);
			}
		} else {
			is_leaf = true;
		}
	}

}
