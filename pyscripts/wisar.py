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

	for i in range(1, len(someList)):

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

def stdv(index, dataArray):
	avg = averageColumn(index,dataArray)
	sumi = 0

	for i in range(0, len(dataArray)):
		element = dataArray[i][index]
		diff = float(element) - avg
		sumi += diff*diff

	return math.sqrt(sumi/len(dataArray))	

def averageColumn(index ,  dataArray):
	
	sumi = 0.0
	n = 0
	for i in range(0, len(dataArray)):
		
		element = dataArray[i][index]
		
		if(element != 'null'):
			sumi += float(dataArray[i][index]);
			n = n+1;

	returnVal = 0;
	if i == 0:
		returnVal =  0
	else:
		returnVal = sumi/n
		

	return returnVal

def removeMetaFile(folder):
	fileList = getFiles(folder)
	for i in range ( 0, len(fileList) ):
		if(fileList[i][fileList[i].rfind('/')+1:] == 'metadata.csv'):
			os.remove(fileList[i])


def isLastElementLessThan(column ,  dataArray, threshold):
	
	sum = 0.0
	n = 0
		
	element = dataArray[len(dataArray)-1][column]
	returnVal = False
	if	element < threshold:
		returnVal = True
		
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

def makeStdvHeader():
	header = [];
	header.append('behavior')
	
	header.append('avgvaveragedotproduct')
	header.append('stdvaveragedotproduct')
	
	header.append('avgtotaldistance')
	header.append('stdvtotaldistance')
	
	header.append('avgfractiondistbelowten')
	header.append('stdvfractiondistbelowten')

	return ','.join(header)

def appendToStdvFile(inputfile, folder):
	data = loadCsv(inputfile)
	
	line = []
	line.append(folder)
	
	line.append(str(averageColumn(1,data)))
	line.append(str(stdv(1,data)))
	
	line.append(str(averageColumn(2,data)))
	line.append(str(stdv(2,data)))

	line.append(str(averageColumn(3,data)))
	line.append(str(stdv(3,data)))
	
	outputFile = 'C:/agentout/stdv.csv'
	
	appendToFile(outputFile , ','.join(line))


#========================================
# runners
#========================================
def runner(folder):
	agentOutPath = 'C:/agentout/'+ folder + '/'
	removeMetaFile(agentOutPath)
	# list of files adaptiveratest2
	fileList = getFiles(agentOutPath)

	metadaFilePath = 'C:/agentout/' + folder + '/metadata.csv'

	appendToFile(metadaFilePath, makeHeader())

	for i in range ( 0, len(fileList) ):

		print ' working file ', fileList[i]

		# get the metada 
		metadata = calcMetadata(fileList[i], 3)

		# append to master file
		appendToFile(metadaFilePath, metadata)


	# stdv appendToFile(outputFile, makeStdvHeader())
	
def addToSdtvFromMetadaFile(folder):
	
	fileList = getFiles('C:/agentout/'+ folder + '/')
	
	for i in range ( 0, len(fileList) ):
		if(fileList[i][fileList[i].rfind('/')+1:] == 'metadata.csv'):
			appendToStdvFile(fileList[i], folder)	

def findFilesWhereAgentWentSouthOfOrigin(folder):
	
	fileList = getFiles('C:/agentout/'+ folder + '/')
	
	for i in range ( 0, len(fileList) ):
		data =loadCsv(fileList[i]);
		if data[len(data)-1][1] < data[0][1]:
			print fileList[i]



# appendToFile('C:/agentout/stdv.csv', makeStdvHeader())
# remove stdv file
# add header 
# call runner on all
# call addToStdfFromMeta on all
addToSdtvFromMetadaFile('adaptive_ra_east_vs')
addToSdtvFromMetadaFile('adaptive_ra_north_vs')
addToSdtvFromMetadaFile('adaptive_ra_south_vs')
addToSdtvFromMetadaFile('adaptive_ra_west_vs')
addToSdtvFromMetadaFile('adaptive_south_north')
addToSdtvFromMetadaFile('adaptive_north_south')
addToSdtvFromMetadaFile('adaptive_east_west_2')
addToSdtvFromMetadaFile('opportune_east')
addToSdtvFromMetadaFile('opportune_south')
addToSdtvFromMetadaFile('opportune_north')
addToSdtvFromMetadaFile('opportune_west')
#print( calculateDistance(-116.828682431865, 40.3770334879529, -116.767784062181, 40.3853990342854) )
