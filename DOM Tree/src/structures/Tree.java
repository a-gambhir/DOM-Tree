package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 *
 */
public class Tree {

	/**
	 * Root node
	 */
	TagNode root=null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;

	/**
	 * Initializes this tree object with scanner for input HTML file
	 *
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object.
	 *
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {

		Stack<TagNode> nodes = new Stack<TagNode>();

		String rootTag = sc.nextLine();
		String bodyTag = sc.nextLine();

		TagNode body = new TagNode(bodyTag.substring(1,bodyTag.length()-1),null,null);

		root = new TagNode(rootTag.substring(1,rootTag.length()-1),body,null);

		nodes.push(root);



		nodes.push(body);



		TagNode ptr = body;


		while(sc.hasNextLine()&&!nodes.isEmpty()){

		String currentLine = sc.nextLine();

		if(currentLine.charAt(0)=='<'&&currentLine.charAt(1)!='/'){


			 if(nodes.peek().sibling!=null){


				nodes.peek().sibling = new TagNode(currentLine.substring(1,currentLine.length()-1),null,null);


				ptr=nodes.peek().sibling;

				nodes.pop();
				nodes.push(ptr);

			}
			 else if(ptr.sibling!=null && nodes.peek().sibling==null){
				 ptr.sibling = new TagNode(currentLine.substring(1,currentLine.length()-1),null,null);
				 nodes.push(ptr.sibling);

				 ptr = ptr.sibling;
			 }
			else {

				ptr.firstChild = new TagNode(currentLine.substring(1,currentLine.length()-1),null,null);
				nodes.push(ptr.firstChild);




				ptr = ptr.firstChild;
			}

		}

		else if(currentLine.charAt(0)!='<'){

			if(nodes.peek().sibling!=null){

				nodes.peek().sibling = new TagNode(currentLine,null,null);


				ptr=nodes.peek().sibling;

				nodes.pop();
			}
			else{
			ptr.firstChild = new TagNode(currentLine,null,null);


			ptr = ptr.firstChild;

			ptr.sibling = new TagNode("temp",null,null);

			}
		}
		else if(currentLine.charAt(0)=='<'&&currentLine.charAt(1)=='/'){

			if(ptr.sibling!=null){
				ptr.sibling=null;
			}

			if(nodes.peek().sibling!=null){
				nodes.peek().sibling=null;
				nodes.pop();
			}
			ptr=nodes.peek();
			TagNode temp = new TagNode("h",null,null);
			nodes.peek().sibling = temp;
			ptr= temp;


		}

		}

		root.sibling = null;
		body.sibling = null;

	}

	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 *
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {


		traverseReplace(root,oldTag,newTag);
	}

	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 *
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {

		traverseBold(root,row);
	}

	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and,
	 * in addition, all the li tags immediately under the removed tag are converted to p tags.
	 *
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		traverseRemove(root,tag);
	}

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 *
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {

		traverseAddTag(root, null, word, tag);
	}

	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 *
	 * @return HTML string, including new lines.
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}

	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");
			}
		}
	}

	/**
	 * Prints the DOM tree.
	 *
	 */
	public void print() {
		print(root, 1);
	}

	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}

	private void traverseReplace(TagNode tree,String oldTag, String newTag){

		for(TagNode ptr = tree; ptr !=null; ptr=ptr.sibling){

			if(ptr.tag.equals(oldTag)){
				//System.out.println("found tag to replace");
				ptr.tag = newTag;
			}
			 //System.out.println(ptr.tag);
			if(ptr.firstChild != null){
				traverseReplace(ptr.firstChild,oldTag,newTag);
			}
		}

	}

	private void traverseBold(TagNode tree, int row){
		//TagNode table = null;
		int currentRow = 1;

		for(TagNode ptr = tree; ptr != null; ptr=ptr.sibling){

			if(ptr.tag.equals("table")){
				//System.out.println(ptr.tag);
				for(TagNode rowNode = ptr.firstChild; rowNode != null; rowNode = rowNode.sibling){
					//System.out.println("row:" + currentRow);
					if(row == currentRow){
						//System.out.println("test");
						for(TagNode colNode = rowNode.firstChild; colNode != null; colNode = colNode.sibling){

							TagNode temp = colNode.firstChild;
							colNode.firstChild = new TagNode("b",temp,null);
						}

					}

					currentRow++;
				}
			}

			if(ptr.firstChild != null){
				traverseBold(ptr.firstChild,row);
			}
		}

	}

private void traverseAddTag(TagNode tree,TagNode node2, String word, String tag){

		for(TagNode ptr = tree; ptr !=null; ptr=ptr.sibling){

			TagNode prev = node2;

			if(ptr.tag.toLowerCase().contains(word.toLowerCase())){

					if(word.equalsIgnoreCase(ptr.tag)){


						if(prev.firstChild == ptr){
						prev.firstChild = new TagNode(tag,ptr,null);
						}
						else if(prev.sibling == ptr){
							prev.sibling = new TagNode(tag,ptr,null);
						}
					}
					else{

						String[] words = ptr.tag.split("\\s+");

						for(int i = 0;i<words.length;i++){

							if(words[i].toLowerCase().contains(word.toLowerCase())){


								if(prev.firstChild == ptr){
									TagNode newNode1 = new TagNode(ptr.tag.substring(0,ptr.tag.indexOf(words[i])),null,null);


									TagNode newNode2 = new TagNode(words[i],null,null);

									TagNode temp = new TagNode(tag,null,null);



										TagNode newNode3 = new TagNode(ptr.tag.substring(ptr.tag.indexOf(words[i])+words[i].length()),null,null);

										if(ptr.tag.indexOf(words[i])==0){
											prev.firstChild = temp;
											temp.firstChild = newNode2;
											temp.sibling = newNode3;
										}
										else{

										prev.firstChild = newNode1;
										newNode1.sibling = temp;
										temp.firstChild = newNode2;
										}
										if(!newNode3.tag.isEmpty()){

										temp.sibling = newNode3;
										}


										if(ptr.sibling!=null&&newNode3.tag.isEmpty()){
											newNode2.sibling = ptr.sibling;
											ptr = newNode2.sibling;
											}
										else if(ptr.sibling!=null&&newNode3.tag.isEmpty()==false){
											newNode3.sibling = ptr.sibling;
											ptr = newNode3.sibling;
										}


								}
								else if(prev.sibling == ptr){
									TagNode newNode1 = new TagNode(ptr.tag.substring(0,ptr.tag.indexOf(words[i])),null,null);
									TagNode newNode2 = new TagNode(words[i],null,null);
									TagNode temp = new TagNode(tag,null,null);


									TagNode newNode3 = new TagNode(ptr.tag.substring(ptr.tag.indexOf(words[i])+words[i].length()),null,null);

									if(ptr.tag.indexOf(words[i])==0){
										prev.sibling = temp;
										temp.firstChild = newNode2;
										temp.sibling = newNode3;
									}
									else{

										prev.sibling = newNode1;
										newNode1.sibling = temp;
										temp.firstChild = newNode2;
									}
										if(!newNode3.tag.isEmpty()){

										temp.sibling = newNode3;

										}

										if(ptr.sibling!=null&&newNode3.tag.isEmpty()){
											newNode2.sibling = ptr.sibling;
											ptr = newNode2.sibling;
											}
										else if(ptr.sibling!=null&&newNode3.tag.isEmpty()==false){
											newNode3.sibling = ptr.sibling;
											ptr = newNode3.sibling;
										}

								}


							}
						}

					}
			}



			if(ptr.firstChild != null){
				prev = ptr;
				traverseAddTag(ptr.firstChild,prev,word,tag);
			}
		}
	}


	private void traverseRemove(TagNode tree, String tag){

		for(TagNode ptr = tree; ptr !=null; ptr=ptr.sibling){

		if(tag.equals("em")||tag.equals("b")||tag.equals("p")){

			if(ptr.sibling!=null&&ptr.sibling.tag.equals(tag)){

					if(ptr.sibling.sibling!=null&&ptr.sibling.firstChild!=null){

						// if child of node getting removed has multiple siblings
						TagNode temp2 = ptr.sibling.firstChild;
						while(temp2.sibling!=null){
							temp2=temp2.sibling;
						}

						TagNode temp = ptr.sibling;
						ptr.sibling=temp.firstChild;
						temp2.sibling = temp.sibling;
					}
					else if(ptr.sibling.sibling==null&&ptr.sibling.firstChild!=null){
						TagNode temp = ptr.sibling;
						ptr.sibling=temp.firstChild;
					}
			}
			else if(ptr.firstChild!=null&&ptr.firstChild.tag.equals(tag)){

					if(ptr.firstChild.firstChild!=null&&ptr.firstChild.sibling==null){
						TagNode temp = ptr.firstChild;
						ptr.firstChild = temp.firstChild;
					}
					else if(ptr.firstChild!=null&&ptr.firstChild.sibling!=null){

						// if child of node getting removed has multiple siblings
						TagNode temp2 = ptr.firstChild.firstChild;
						while(temp2.sibling!=null){
							temp2=temp2.sibling;
						}

						TagNode temp = ptr.firstChild;
						ptr.firstChild = temp.firstChild;
						temp2.sibling = temp.sibling;
					}
				}
			}
			else if(tag.equals("ol")||tag.equals("ul")){

				if(ptr.sibling!=null&&ptr.sibling.tag.equals(tag)){

					if(ptr.sibling.sibling!=null&&ptr.sibling.firstChild!=null){

						// if child of node getting removed has multiple siblings
						TagNode temp2 = ptr.sibling.firstChild;
						while(temp2.sibling!=null){
							traverseReplace(temp2,"li","p");
							temp2=temp2.sibling;
						}

						TagNode temp = ptr.sibling;
						ptr.sibling=temp.firstChild;
						temp2.sibling = temp.sibling;
					}
					else if(ptr.sibling.sibling==null&&ptr.sibling.firstChild!=null){

						// if child of node getting removed has multiple siblings
						TagNode temp2 = ptr.sibling.firstChild;
						while(temp2.sibling!=null){
							traverseReplace(temp2,"li","p");
							temp2=temp2.sibling;
						}

						TagNode temp = ptr.sibling;
						ptr.sibling=temp.firstChild;
					}
			}
			else if(ptr.firstChild!=null&&ptr.firstChild.tag.equals(tag)){

					if(ptr.firstChild.firstChild!=null&&ptr.firstChild.sibling==null){

						// if child of node getting removed has multiple siblings
						TagNode temp2 = ptr.firstChild.firstChild;
						while(temp2.sibling!=null){
							traverseReplace(temp2,"li","p");
							temp2=temp2.sibling;
						}

						TagNode temp = ptr.firstChild;
						ptr.firstChild = temp.firstChild;
					}
					else if(ptr.firstChild!=null&&ptr.firstChild.sibling!=null){

						// if child of node getting removed has multiple siblings
						TagNode temp2 = ptr.firstChild.firstChild;
						while(temp2.sibling!=null){
							traverseReplace(temp2,"li","p");
							temp2=temp2.sibling;
						}

						TagNode temp = ptr.firstChild;
						ptr.firstChild = temp.firstChild;
						temp2.sibling = temp.sibling;
					}
				}

			}



			if(ptr.firstChild != null){
				traverseRemove(ptr.firstChild,tag);
			}
		}

	}

}
