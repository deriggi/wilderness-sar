import os
import math

def getFiles(folder):
	children = os.listdir(folder)
	filesOnly = []
	for c in children:
		filesOnly.append(folder + c )
	return filesOnly


def appendToFile(filePath, line):
	fileHandle = open(filePath, 'a')
	fileHandle.write(line+'\n');
	fileHandle.close()

def calcMetadata(singleFile, column):
	data =loadCsv(singleFile);

	distance = calculateDistance(data[0][0], data[0][1], data[len(data)-1][0], data[len(data)-1][1])
	averageDp = averageColumn(column, data)
	countBelow = fractionBelowThreshold(2, data, 10)

	simid = singleFile[singleFile.rfind('/')+1:singleFile.rfind('.')]

	line = []
	line.append(str(simid))
	line.append(str(averageDp))
	line.append(str(distance))
	line.append(str(countBelow))

	metadataLine = ','.join(line) 

	return metadataLine


def loadCsv(filename):
	openFile = open(filename, 'r')
	someList = list(openFile)
	openFile.close()

	theData = []

	for i in range(0, len(someList)):

		lineParts = someList[i].split(',')
		theData.append(lineParts)

	return theData

def fractionBelowThreshold(index, dataArray, threshold):
	countBelow = 0;
	total = 0
	for i in range(0, len(dataArray)):

		element = dataArray[i][index]
		
		if(element != 'null' and float(element) < threshold):
			countBelow = countBelow + 1;
		if element != 'null' :
			total = total + 1;	

	return float(countBelow)/float(total)


def averageColumn(index ,  dataArray):
	
	sum = 0.0
	n = 0
	for i in range(0, len(dataArray)):
		
		element = dataArray[i][index]
		
		if(element != 'null'):
			sum += float(dataArray[i][index]);
			n = n+1;

	returnVal = 0;
	if i == 0:
		returnVal =  0
	else:
		returnVal = sum/n
		print "dividing by ", n

	return returnVal

def calculateDistance(lon1, lat1, lon2, lat2):
	R = 6371
	lon1 = float(lon1)
	lat1 = float(lat1)
	lon2 = float(lon2)
	lat2 = float(lat2)

	dlon = math.radians(float(lon2)- float(lon1))
	dlat = math.radians(float(lat2)- float(lat1))

	a = math.sin(dlat/2) * math.sin(dlat/2) + math.sin(dlon/2) * math.sin(dlon/2) * math.cos(lat1) * math.cos(lat2); 
	c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a)); 
	d = R * c;
	return d

def makeHeader():
	header = [];
	header.append('simid')
	header.append('averagedotproduct')
	header.append('totaldistance')
	header.append('fractiondistbelowten')

	return ','.join(header)

def runner():

	# list of files
	fileList = getFiles('C:/agentout/adaptiveratest2/')

	appendToFile('C:/agentout/adaptiveratest2/metadata.csv', makeHeader())

	for i in range ( 0, len(fileList) ):

		print ' working file ', fileList[i]

		# get the metada 
		metadata = calcMetadata(fileList[i], 3)

		# append to master file
		appendToFile('C:/agentout/adaptiveratest2/metadata.csv', metadata)

runner()
