package com.hm.newAge.services;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.code_factory.jpa.nestedset.JpaNestedSetManager;
import org.code_factory.jpa.nestedset.NestedSetManager;
import org.code_factory.jpa.nestedset.Node;

import com.hm.newAge.VOs.CurriculumTree;
import com.hm.newAge.VOs.TreeNode;


public class TopicAutoSuggest {
	
	
	public TreeNode getTreeTest(String country,String level,String stream,String subject) {
		EntityManagerFactory emf=Persistence.createEntityManagerFactory("NewAge-Test");
		EntityManager em=emf.createEntityManager();
		
		NestedSetManager nsm = new JpaNestedSetManager(em);
		TreeNode subjectTreeNode = null;
		
		try{
			Node<CurriculumTree> root = nsm.fetchTree(CurriculumTree.class,0);
			Node<CurriculumTree> countryNode = root.getChildren(country).get(0);
			Node<CurriculumTree> levelNode = countryNode.getChildren(level).get(0);
			Node<CurriculumTree> streamNode = levelNode;
			if(!stream.equalsIgnoreCase("")) streamNode = levelNode.getChildren(stream).get(0);
			Node<CurriculumTree> subjectNode = streamNode.getChildren(subject).get(0);
	        subjectTreeNode = getTreeNode(subjectNode,0);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally{
			em.close();
			emf.close();
		}
		
		return subjectTreeNode;
	}
	private TreeNode getTreeNode(Node<CurriculumTree> node,int depth) {
		TreeNode treeNode = new TreeNode(node.unwrap().getName());
		
		if(node.hasChildren()) {
			ArrayList<TreeNode> children = new ArrayList<TreeNode>();
			for(Node<CurriculumTree> childNode : node.getChildren()) {
				TreeNode childTreeNode = getTreeNode(childNode,depth+1);
				children.add(childTreeNode);
			}
			treeNode.setChildren(children);
		}
		
		return treeNode;	
	}

}
