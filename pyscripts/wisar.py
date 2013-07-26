import os
import math

def getFiles(folder):
	children = os.listdir(folder)
	filesOnly = []
	for c in children:
		filesOnly.append(folder + '/'+ c )
	return filesOnly


def getLeafFolderName(folderPath):
	folder = folderPath[folderPath.rfind('/')+1:]
	return folder

def appendToFile(filePath, line):
	fileHandle = open(filePath, 'a')
	fileHandle.write(line+'\n');
	fileHandle.close()

def calcMetadata(singleFile, column, meanCenterLastPoint):
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
	line.append(str(calculateDistance(meanCenterLastPoint[0], meanCenterLastPoint[1], data[len(data)-1][0], data[len(data)-1][1])))

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

def sqrtAvgSumSquares(index, dataArray):
	
	sumi = 0

	for i in range(0, len(dataArray)):
		element = dataArray[i][index]
		sumi += float(element)*float(element)

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
	header.append('distancetomeancenterlastpoint')

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

	header.append('avgdistancetomeancenterlastpoint')
	header.append('standarddistancelastpoint')

	

	return ','.join(header)

def appendToStdvFile(inputfile, folder, outName, outputRoot):
	data = loadCsv(inputfile)
	
	line = []
	line.append(getLeafFolderName(folder))
	
	line.append(str(averageColumn(1,data)))
	line.append(str(stdv(1,data)))
	
	line.append(str(averageColumn(2,data)))
	line.append(str(stdv(2,data)))

	line.append(str(averageColumn(3,data)))
	line.append(str(stdv(3,data)))
	
	line.append(str(averageColumn(4,data)))
	line.append(str(sqrtAvgSumSquares(4,data)))
	
	


	
	outputFile = outputRoot+outName+'.csv'
	
	appendToFile(outputFile , ','.join(line))


#========================================
# runners
#========================================
def summaraizeRoutes(agentOutPath):
	
	removeMetaFile(agentOutPath)
	# list of files adaptiveratest2
	fileList = getFiles(agentOutPath)

	metadaFilePath = agentOutPath + '/metadata.csv'

	appendToFile(metadaFilePath, makeHeader())
	meanCenter = meanCenterLastPoint(agentOutPath)

	for i in range ( 0, len(fileList) ):

		print ' working file ', fileList[i]

		# get the metada 
		metadata = calcMetadata(fileList[i], 3, meanCenter)

		# append to master file
		appendToFile(metadaFilePath, metadata)


	
	
def summaraizeBehaviors(folder, outName, outputRoot):
	

	fileList = getFiles(folder)

	for i in range ( 0, len(fileList) ):
		if(fileList[i][fileList[i].rfind('/')+1:] == 'metadata.csv'):
			appendToStdvFile(fileList[i], folder, outName, outputRoot ) 	

def findFilesWhereAgentWentSouthOfOrigin(folder):
	
	fileList = getFiles(folder)
	
	for i in range ( 0, len(fileList) ):
		data =loadCsv(fileList[i]);
		if data[len(data)-1][1] < data[0][1]:
			print fileList[i]


def meanCenterLastPoint(folder):
	fileList = getFiles(folder)

	sumx=0
	sumy=0
	tempData = None
	for i in range( 0, len(fileList)):
		if(fileList[i][fileList[i].rfind('/')+1:] != 'metadata.csv'):
			tempData = loadCsv(fileList[i])
			sumx += float(tempData[len(tempData)-1][0])
			sumy += float(tempData[len(tempData)-1][1])
		# avg sum last x, last y
	
	avgx = sumx/i
	avgy = sumy/i

	meancenter = []
	meancenter.append(avgx)
	meancenter.append(avgy)
	
	return meancenter





#===============================
# summarize each behavior
#===============================
outputRoot = 'C:/agentout/'
spot = "SPOT_1"
def runBehaviorSummary(outputRoot, spot):
	if os.path.isfile(outputRoot + spot + '.csv'):
		os.remove(outputRoot + spot + '.csv')
	rootPath = outputRoot + spot + '/'
	appendToFile(outputRoot + spot + '.csv', makeStdvHeader())
	folderList = os.listdir(rootPath)
	for child in folderList:
		if(os.path.isdir(rootPath + child )):
			summaraizeBehaviors(rootPath +child, spot, outputRoot)


#===============================
# collec metadata for each route
#===============================
def runRouteSummary(outputRoot, spot):
	rootPath = outputRoot + spot+ '/'
	folderList = os.listdir(rootPath)
	for child in folderList:
		if(os.path.isdir(rootPath + child )):
			summaraizeRoutes(rootPath +child)


runRouteSummary(outputRoot, spot)
runBehaviorSummary(outputRoot, spot)

# todo: compare the last points of every route in a spot to see how different the algos are