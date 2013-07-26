import os
import math

def loadCsv(filename):
	openFile = open(filename, 'r')
	someList = list(openFile)
	openFile.close()

	theData = []

	for i in range(1, len(someList)):

		lineParts = someList[i].split(',')
		theData.append(lineParts)

	return theData

def getFiles(folder):
	children = os.listdir(folder)
	filesOnly = []
	for c in children:
		filesOnly.append(folder + '/'+ c )
	return filesOnly

def runner(root):
	children = getFiles(root)

	behaviors = {}
	for c in children:
		applyRanks(behaviors,loadCsv(c), 1.0/len(children))

	return behaviors

def appendToFile(filePath, line):
	fileHandle = open(filePath, 'a')
	fileHandle.write(line+'\n');
	fileHandle.close()


def writeRanks(ranks, outFile):
	for key in ranks:
		appendToFile(outfile, str(ranks[key]) + ',' + key)


def applyRanks(behaviors, dataFrame, weight):
	
	for i in range (0, len(dataFrame)):
		if dataFrame[i][1] not in behaviors:
			behaviors[dataFrame[i][1]] = 0
		
		behaviors[dataFrame[i][1]] += int(dataFrame[i][0])


# give this a folder to a list of csvs with ranks
ranks = runner('C:/agentout/rank/')
outfile = 'C:/agentout/rank/rankoutput.csv'
if os.path.isfile(outfile):
	os.remove(outfile)
writeRanks(ranks, outfile)