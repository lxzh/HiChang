package hichang.Song;
public class MyQueue {  
      
    /** 
     * 双向链表结构 
     */  
    public class LinkNode {  
  
        // 真正的数据域  
        private Song date;  
  
        // 记录上一个节点  
        private LinkNode prevLinkNode;  
  
        // 记录下一个节点  
        private LinkNode nextLinkNode;  
  
        public LinkNode() {  
  
        }  
  
        public LinkNode(Song date, LinkNode prevLinkNode, LinkNode nextLinkNode) {  
            this.date = date;  
            this.prevLinkNode = prevLinkNode;  
            this.nextLinkNode = nextLinkNode;  
        }  
        
        public boolean equals(Song song){
     	   return (this.date.getSongID()==song.getSongID());
           }
    }  
      
    // 结点个数  
    private int nodeSize;  
  
    // 头结点  
    private LinkNode headNode;  
  
    // 尾巴节点  
    private LinkNode tailNode;  
      
    public MyQueue(){  
        headNode = null;  
        tailNode = null;  
    }  
  
    /**
     * 将某元素置于队列顶部
     */
    public boolean top(Song obj){
    	LinkNode link = this.findObject(obj);
    	if(link==headNode){
    		return true;
    	}
    	else if(link==tailNode){
    		tailNode.prevLinkNode.nextLinkNode = null;
    		link.prevLinkNode = null;
    		headNode.prevLinkNode = link;
    		link.nextLinkNode = headNode;
    		headNode = link;
    		return true;
    	}
    	else{
    		LinkNode temp = link.prevLinkNode;
        	temp.nextLinkNode = link.nextLinkNode;
        	link.nextLinkNode.prevLinkNode = temp;
        	link.prevLinkNode = null;
        	link.nextLinkNode = headNode;
        	headNode = link;
        	return true;
    	}
    	
    }
    
    /**
     * 删除已选曲目
     */
    public boolean deleteSelected(Song obj){
    	LinkNode temp;
    	temp = this.findObject(obj);
    	if(nodeSize==1){
    		temp.date = null;
    		temp.prevLinkNode = null;
    		temp.nextLinkNode = null;
    		headNode = null;
    		tailNode = null;
    		nodeSize = 0;
    		return true;
    	}
    	else{
	    	if(temp==headNode){
	    		temp.nextLinkNode.prevLinkNode = null;
	    		headNode = temp.nextLinkNode;
	    		temp.date = null;
	    		temp.nextLinkNode = null;
	    		temp.prevLinkNode = null;
	    		nodeSize--;
	    		return true;
	    	}
	    	if(temp==tailNode){
	    		LinkNode temp1 = tailNode.prevLinkNode;
	    		temp1.nextLinkNode = null;
	    		tailNode.date = null;
	    		tailNode.prevLinkNode = null;
	    		tailNode = temp1;
	    		temp1.prevLinkNode.nextLinkNode = tailNode;
	    		nodeSize--;
	    		return true;
	    	}
	    	else{
	    		temp.prevLinkNode.nextLinkNode = temp.nextLinkNode;
	    		temp.nextLinkNode.prevLinkNode = temp.prevLinkNode;
	    		temp.prevLinkNode = null;
	    		temp.nextLinkNode = null;
	    		temp.date = null;
	    		nodeSize--;
	    		return true;
	    	}
    	}
    }
    
    /** 
     * 添加元素 
     */  
    public boolean add(Song element) {  
        if (nodeSize == 0) {  
            headNode = new LinkNode(element, null, tailNode);  
        }else {  
  
            if (tailNode == null) {  
                tailNode = new LinkNode(element, headNode, null);  
                headNode.nextLinkNode = tailNode;  
                nodeSize++;  
                return true;  
            }  
  
            LinkNode linkNode = tailNode;  
            tailNode = new LinkNode(element, linkNode, null);  
            linkNode.nextLinkNode = tailNode;  
        }  
        nodeSize++;  
        return true;  
    }  
      
    public Song poll() {  
          
        LinkNode headNodeTemp = headNode;  
        Song date = headNodeTemp.date;  
        if(headNode.nextLinkNode == null){  
            headNode.date = null;  
            headNode = null;  
            nodeSize--;  
            return date;  
        }else{  
            headNode = headNode.nextLinkNode;  
            if(headNode == tailNode){  
                tailNode = null;  
            }  
        }  
          
        nodeSize--;  
          
        return headNodeTemp.date;  
    }  
    
    /** 
     * 清除所有元素 
     */  
    public void clear() {  
    	
        LinkNode linkNodeNowTemp = headNode;  
        for (int i = 0; i < nodeSize; i++) {  
        	headNode = linkNodeNowTemp.nextLinkNode;
        	linkNodeNowTemp.prevLinkNode = null;
        	linkNodeNowTemp.date = null;
        	linkNodeNowTemp.nextLinkNode = null;
        }
        headNode = null;  
        tailNode = null;  
        nodeSize = 0;  
    }  

    /** 
     * 判断是否存在 ,存在返回
     */  
    public LinkNode findObject(Song object) {  
  
        LinkNode linkNodeNowTemp = headNode;  
  
        for (int i = 0; i < nodeSize; i++) {  
  
            if (object.getSongID() == linkNodeNowTemp.date.getSongID()) {  
            	break;
            }  
  
            linkNodeNowTemp = linkNodeNowTemp.nextLinkNode;  
        }  
        return linkNodeNowTemp;     
    }  
      
    /** 
     * 队列是否为空 
     */  
    public boolean isEmpty() {  
        // TODO Auto-generated method stub  
        return nodeSize == 0;  
    }  
  
    public int size() {  
        // TODO Auto-generated method stub  
        return nodeSize;  
    }  
      
    /** 
     * 根据索引号查找节点 
     *  
     * @param index 
     * @return 
     */  
    public LinkNode findLinkNodeByIndex(int index) {  
  
        LinkNode linkNodeNowTemp = headNode;  
  
        for (int i = 0; i < nodeSize; i++) {  
  
            if (i == index) {  
                return linkNodeNowTemp;  
            }  
  
            linkNodeNowTemp = linkNodeNowTemp.nextLinkNode;  
        }  
        return null;  
    }  
      
    @Override  
    public String toString() {  
  
        StringBuffer str = new StringBuffer("[");  
        LinkNode linkNode = null;  
        for (int i = 0; i < nodeSize; i++) {  
  
            linkNode = findLinkNodeByIndex(i);  
  
            str.append("[" + linkNode.date + "],");  
  
        }  
  
        if (nodeSize > 0) {  
            return str.substring(0, str.lastIndexOf(",")) + "]";  
        }  
  
        return str.append("]").toString();  
    }
    


      
}  
