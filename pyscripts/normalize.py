import os

def loadCsv(filename, offset=0):
	openFile = open(filename, 'r')
	someList = list(openFile)
	openFile.close()
	theData = []

	for i in range(offset, len(someList)):

		lineParts = someList[i].split(',')
		theData.append(lineParts)

	return theData

def getMaxMin(data):
	maxval = -1000
	minval = 10000
	for i in range(0, len(data)):
		for j in range(0,len(data[i])):
			x = float(data[i][j])
			if x > maxval:
				maxval = x
			if x < minval:
				minval = x

	return [maxval, minval]

def normalize(data, maxval, minval):
	# ( x - min )/ (max - min)

	for i in range(0, len(data)):
		for j in range(0,len(data[i])):
			x = float(data[i][j])
			norm = ( x -  minval)/(maxval - minval)
			data[i][j] = str(norm)

def appendToFile(filePath, line, newline =True):
	fileHandle = open(filePath, 'a')

	fileHandle.write(line);
	if newline:
		fileHandle.write('\n');

	fileHandle.close()

def getHeaderFromFile(filepath):
	openFile = open(filepath, 'r')
	someList = list(openFile)
	openFile.close()
	return someList[0];

def writeDataToCsv(header, data, path):
	appendToFile(path, header, False)
	for i in range(0, len(data)):
		
		# [str(x) for x in data[i]]
		
		line = ','.join(data[i])
		appendToFile(path, line)


inputfile = 'C:/agentout/agentresultsbystat/totaldistancespot1.csv'
data = loadCsv(inputfile,1)
maxmin = getMaxMin(data)
normalize(data, maxmin[0], maxmin[1])
writeDataToCsv(getHeaderFromFile(inputfile), data,'C:/agentout/agentresultsbystat/normalisedSpot1.csv')

