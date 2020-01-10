import java.util.ArrayList;
import java.util.Iterator;

public class KDTree implements Iterable<Datum>{ 

	KDNode 		rootNode;
	int    		k; 
	int			numLeaves;
	
	// constructor

	public KDTree(ArrayList<Datum> datalist) throws Exception {

		Datum[]  dataListArray  = new Datum[ datalist.size() ]; 

		if (datalist.size() == 0) {
			throw new Exception("Trying to create a KD tree with no data");
		}
		else
			this.k = datalist.get(0).x.length;

		int ct=0;
		for (Datum d :  datalist) {
			dataListArray[ct] = datalist.get(ct);
			ct++;
		}
		
	//   Construct a KDNode that is the root node of the KDTree.

		rootNode = new KDNode(dataListArray);
	}
	
	//   KDTree methods
	
	public Datum nearestPoint(Datum queryPoint) {
		return rootNode.nearestPointInNode(queryPoint);
	}
	
	public int height() {
		return this.rootNode.height();	
	}

	public int countNodes() {
		return this.rootNode.countNodes();	
	}
	
	public int size() {
		return this.numLeaves;	
	}

	//-------------------  helper methods for KDTree   ------------------------------

	public static long distSquared(Datum d1, Datum d2) {

		long result = 0;
		for (int dim = 0; dim < d1.x.length; dim++) {
			result +=  (d1.x[dim] - d2.x[dim])*((long) (d1.x[dim] - d2.x[dim]));
		}
		// if the Datum coordinate values are large then we can easily exceed the limit of 'int'.
		return result;
	}

	public double meanDepth(){
		int[] sumdepths_numLeaves =  this.rootNode.sumDepths_numLeaves();
		return 1.0 * sumdepths_numLeaves[0] / sumdepths_numLeaves[1];
	}
	
	// I added the following methods:
	
	private static int getMaxValue(int[] numbers){ // get max integer in an array of INTEGERS
		  int maxValue = numbers[0];
		  for (int i = 1; i < numbers.length; i ++){
		    if (numbers[i] > maxValue){
			  maxValue = numbers[i];
			}
		  }
		  return maxValue;
		}
	
	private static int getMinValue(int[] numbers){ // get min integer in an array of INTEGERS
		  int minValue = numbers[0];
		  for(int i = 1; i < numbers.length; i ++){
		    if (numbers[i] < minValue){
			  minValue = numbers[i];
			}
		  }
		  return minValue;
		}
		
	class KDNode { 

		boolean leaf;
		Datum leafDatum;           //  only stores Datum if this is a leaf
		
		//  the next two variables are only defined if node is not a leaf

		int splitDim;      // the dimension we will split on
		int splitValue;    // datum is in low if value in splitDim <= splitValue, and high if value in splitDim > splitValue  

		KDNode lowChild, highChild;   //  the low and high child of a particular node (null if leaf)
		  //  You may think of them as "left" and "right" instead of "low" and "high", respectively

		KDNode(Datum[] datalist) throws Exception{
			
			/*
			 *  This method takes in an array of Datum and returns 
			 *  the calling KDNode object as the root of a sub-tree containing  
			 *  the above fields.
			 */

			//   ADD YOUR CODE BELOW HERE
			
			/*
			 * System.out.println(); System.out.println("*** NEW ***");
			 * System.out.println();
			 */
			 
			
			int[] samedimension = new int[datalist.length];
			int[] ranges = new int[k];
						
			/*
			 * System.out.print("Datalist is: "); for (int i = 0; i < datalist.length; i ++)
			 * { System.out.print(datalist[i]); }
			 * 
			 * System.out.println();
			 * 
			 * System.out.print("Datalist length: "); System.out.println(datalist.length);
			 */
			 
			//System.out.print("Dimension: "); System.out.println(k);
					
			if (datalist.length == 0) { // if the datalist is empty
				//System.out.println("The length is 0.");
				this.leaf = false;
				this.leafDatum = null;
				this.lowChild = null;
				this.highChild = null;
				numLeaves = 0;
			} else if (datalist.length == 1) { // if the array contains only one data point, then the node is a leaf node
				this.leaf = true;
				this.leafDatum = datalist[0];
				this.lowChild = null;
				this.highChild = null;
				numLeaves ++;
				/*
				 * System.out.println("datalist length is 1."); System.out.println("numLeaves: "
				 * + numLeaves); System.out.println("this.leaf: " + this.leaf);
				 * System.out.println("this.leafDatum: " + this.leafDatum);
				 */
			}			
			else {
				this.leaf = false;
				//System.out.println("This is the else clause.");
				for (int i = 0; i < k; i ++) {
					//System.out.print("samedimension: ");
					for (int j = 0; j < datalist.length; j ++) {
						samedimension[j] = datalist[j].x[i];
						//System.out.print(" " + samedimension[j] + " ");	
					}
					int range = getMaxValue(samedimension) - getMinValue(samedimension); // obtain the range of the dimension
					ranges[i] = range; // store the range of each dimension in an array of ranges (integers)
					
					/*
					 * System.out.println(); System.out.print("Max value: ");
					 * System.out.println(getMaxValue(samedimension));
					 * System.out.print("Min value: ");
					 * System.out.println(getMinValue(samedimension));
					 * System.out.print("Range is: "); System.out.println(range);
					 */
				}
				
				// now find the biggest range in the array of ranges
				
				int hugerange = getMaxValue(ranges);
								
				// now use hugerange to iterate through the array of ranges and find index of hugerange in array of ranges
				
				for (int i = 0; i < ranges.length; i ++) {
					if (hugerange == ranges[i]) {
						this.splitDim = i; // this is the splitDim
						break;
					}
				}
				
				//System.out.println("The biggest range is " + hugerange + " in splitting dimension " + splitDim + "."); 
				
				// now, we need to find the splitValue
				
				// first we need to the find the average of the min and max values in the splitting dimension
					// first, get an array of all the integers that are part of the splitting dimension
				
				int[] splitdimArray = new int[datalist.length]; 
				
				for (int i = 0; i < datalist.length; i ++) {
					splitdimArray[i] = datalist[i].x[this.splitDim];
				}
				
				// now compute the average (splitValue) of the min and max values in the splitting dimension
				
				this.splitValue = Math.floorDiv(getMaxValue(splitdimArray) + getMinValue(splitdimArray), 2);
																			
				//System.out.println("Splitting value (Average method): " + splitValue);
				
				if (hugerange == 0) { // this removes duplicates
					this.leaf = true;
					this.leafDatum = datalist[0];
					this.lowChild = null;
					this.highChild = null;
					numLeaves ++;
					/*
					 * System.out.println("WE HAVE DUPLICATES HERE");
					 * System.out.println("this.leaf: " + this.leaf);
					 * System.out.println("this.leafDatum: " + this.leafDatum);
					 * System.out.println("numLeaves: " + numLeaves);
					 */
				} else {
					// now we will begin to split
					
					int countlow = 0; // amount of datums in lowchilddata
					int counthigh = 0; // amount of datums in highchilddata
					
					for (int i = 0; i < datalist.length; i++) {
						if (datalist[i].x[this.splitDim] <= this.splitValue) { // if the value in the splitDim is less than or equal to the splitValue, 
							countlow ++;
						} else { // if the value in the splitDim is bigger than the splitValue
							counthigh ++;
						}
					}
					
					//System.out.println("Counlow: " + countlow);
					//System.out.println("Counthigh: " + counthigh);
					
					Datum[] lowchilddata = new Datum[countlow];
					Datum[] highchilddata = new Datum[counthigh];
					
					int t = 0;
					int z = 0;
					
					for (int i = 0; i < datalist.length; i++) {
						if (datalist[i].x[this.splitDim] <= this.splitValue) { // if the value in the splitDim is less than or equal to the splitValue 
							lowchilddata[t] = datalist[i];
							t++;
						} else { // if the value in the splitDim is bigger than the splitValue
							highchilddata[z] = datalist[i];
							z++;
						}
					}
										
					 // System.out.println("Rootnode: " + rootNode);
					  
					/*
					 * System.out.print("Lowchild is: "); for (int i = 0; i < lowchilddata.length; i
					 * ++) { System.out.print(lowchilddata[i]); } System.out.println();
					 * System.out.print("Highchild is: "); for (int i = 0; i < highchilddata.length;
					 * i ++) { System.out.print(highchilddata[i]); } System.out.println();
					 * System.out.println();
					 */
					
					this.lowChild = new KDNode(lowchilddata);
					
					this.highChild = new KDNode(highchilddata);		
							
				}
			
			}
			
		}	

			//   ADD YOUR CODE ABOVE HERE
				
		public Datum nearestPointInNode(Datum queryPoint) {
			Datum nearestPoint, nearestPoint_otherSide;
		
			//   ADD YOUR CODE BELOW HERE
			
			KDNode thisside;
			KDNode otherside;
									
			if (this.leaf) { // BASE CASE
				return this.leafDatum;
			}
			
			else {
				
//				int compare = queryPoint.x[this.splitDim];
				
				if ( (queryPoint.x[this.splitDim]) <= this.splitValue) { // lowChild case
					thisside = this.lowChild;
					otherside = this.highChild;
				} 
				else {
					thisside = this.highChild;
					otherside = this.lowChild;
				}
				
				nearestPoint = thisside.nearestPointInNode(queryPoint);
				
				long NPtoQP = distSquared(nearestPoint, queryPoint);
				long distToBoundary = (long) Math.pow( (queryPoint.x[this.splitDim]) - this.splitValue, 2); // to improve speed

				if ( NPtoQP < distToBoundary ) {
					return nearestPoint;	
				}
				else {
					nearestPoint_otherSide = otherside.nearestPointInNode(queryPoint);
//					long NPOtoQP = distSquared(queryPoint, nearestPoint_otherSide);
					if (distSquared(queryPoint, nearestPoint_otherSide) < NPtoQP) {
						return nearestPoint_otherSide;
					}
					else {
						return nearestPoint;
					}
				}
			}
						
			//   ADD YOUR CODE ABOVE HERE

		}
		
		// -----------------  KDNode helper methods (might be useful for debugging) -------------------

		public int height() {
			if (this.leaf) 	
				return 0;
			else {
				return 1 + Math.max( this.lowChild.height(), this.highChild.height());
			}
		}

		public int countNodes() {
			if (this.leaf)
				return 1;
			else
				return 1 + this.lowChild.countNodes() + this.highChild.countNodes();
		}
		
		/*  
		 * Returns a 2D array of ints.  The first element is the sum of the depths of leaves
		 * of the subtree rooted at this KDNode.   The second element is the number of leaves
		 * this subtree.    Hence,  I call the variables  sumDepth_size_*  where sumDepth refers
		 * to element 0 and size refers to element 1.
		 */
				
		public int[] sumDepths_numLeaves(){
			int[] sumDepths_numLeaves_low, sumDepths_numLeaves_high;
			int[] return_sumDepths_numLeaves = new int[2];
			
			/*     
			 *  The sum of the depths of the leaves is the sum of the depth of the leaves of the subtrees, 
			 *  plus the number of leaves (size) since each leaf defines a path and the depth of each leaf 
			 *  is one greater than the depth of each leaf in the subtree.
			 */
			
			if (this.leaf) {  // base case
				return_sumDepths_numLeaves[0] = 0;
				return_sumDepths_numLeaves[1] = 1;
			}
			else {
				sumDepths_numLeaves_low  = this.lowChild.sumDepths_numLeaves();
				sumDepths_numLeaves_high = this.highChild.sumDepths_numLeaves();
				return_sumDepths_numLeaves[0] = sumDepths_numLeaves_low[0] + sumDepths_numLeaves_high[0] + sumDepths_numLeaves_low[1] + sumDepths_numLeaves_high[1];
				return_sumDepths_numLeaves[1] = sumDepths_numLeaves_low[1] + sumDepths_numLeaves_high[1];
			}	
			return return_sumDepths_numLeaves;
		}
		
	}

	public Iterator<Datum> iterator() {
		return new KDTreeIterator(this);
	}
	
	private class KDTreeIterator implements Iterator<Datum> {
		
		//   ADD YOUR CODE BELOW HERE
		
		KDTreeIterator(KDTree sometree) { // constructor
			getleaf(sometree.rootNode);
		}
				
		Datum [] bunchofdatums = new Datum[size()]; // size method returns numLeaves
		int hermione = 0;
		
		public void getleaf(KDNode somenode) { // inorder traversal (get leaves from left to right)
			if (somenode == null) {
				return;
			}
			if (somenode.leaf) { // base case for recursion
				bunchofdatums[hermione] = somenode.leafDatum;
				hermione ++;
			} else {
				getleaf(somenode.lowChild);
				getleaf(somenode.highChild);
			}
		}
		
		public boolean hasNext() {
			return (harry < bunchofdatums.length);
		}
		
		int harry = 0;
		
		public Datum next() {
			
			Datum cur = bunchofdatums[harry];
			harry ++;
			return cur;
			
		}
		
		//   ADD YOUR CODE ABOVE HERE

	}

}

