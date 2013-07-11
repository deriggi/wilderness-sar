  def furthestFromSeg(self):
        '''
            get the point that is furthest from the segment spanning this
            polyline
        '''
        if self.size < 3:
            return None
        else:
            # get an end to end segment
            bigSegment = self.segStartEnd()
            
            # initialize some minimums
            biggestDistance = -1.0
            indexOfFurthestPoint = -1
            
            # start index at second element
            index = 1
            while index < len(self.getPoints() ):
                
                # get the point and distance
                aPoint = self.getPoint(index)
                distanceFromSegment = bigSegment.pointDistance(aPoint)
                
                # if further than or last biggest then save it
                if distanceFromSegment > biggestDistance:
                    biggestDistance = distanceFromSegment
                    indexOfFurthestPoint = index
                
                # increment the index no matter what
                index = index+1
            
            # out of the loop so let's create the DPoint
            furthestPoint = self.getPoint(indexOfFurthestPoint)
            dpoint =  DPoint(furthestPoint.get_x(), furthestPoint.get_y(), biggestDistance, indexOfFurthestPoint)
                        
        return dpoint    
        #return self.getPoint(indexOfFurthestPoint) 