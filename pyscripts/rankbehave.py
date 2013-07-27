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

def loadCsvStrings(filename):
	openFile = open(filename, 'r')
	someList = list(openFile)
	openFile.close()

	return someList


def getFiles(folder):
	children = os.listdir(folder)
	filesOnly = []
	for c in children:
		filesOnly.append(folder + '/'+ c )
	return filesOnly

def getHeaderFromFile(somefile):
	openfile = open(somefile, 'r')
	firstline = list(openfile)[0];
	openfile.close()

	return firstline

def runner():
	outfile = 'C:/agentout/rank/allspotsummary.csv'
	children = []
	children.append('C:/agentout/SPOT_1.csv')
	children.append('C:/agentout/SPOT_2.csv')
	
	headerLine =  getHeaderFromFile('C:/agentout/SPOT_1.csv')
	linelength = len(headerLine.split(','))

	dicts = []

	for i in range (1, linelength):
		dicts.append(averageColumns(i,children))


	#todo ,write these to a summary file!
	if os.path.isfile(outfile):
		os.remove(outfile)

	appendToFileRaw(outfile,headerLine)
	writeDictList(mergeDictionaries(dicts) , outfile)

# def fromDictEntryToLine()
def writeDictList(dictlist, outfile):
	for key in dictlist:
		line=[]
		line.append(key)
		line.extend(dictlist[key])
		line = [str(x) for x in line]
		csvline = ",".join(line)
		appendToFile(outfile,csvline)



def appendToFile(filePath, line):
	fileHandle = open(filePath, 'a')
	fileHandle.write(line+'\n');
	fileHandle.close()

def appendToFileRaw(filePath, line):
	fileHandle = open(filePath, 'a')
	fileHandle.write(line);
	fileHandle.close()

# average the column in the file set
def averageColumns(index, files):
	behaves = {}
	for i in range(0, len(files)):
		data = loadCsv(files[i])
		for j in range (0, len(data)):
			if data[j][0] not in behaves:
				behaves[ data[j][0] ] = 0
			behaves[ data[j][0] ] += float(data[j][index])

	for key in behaves:
		behaves[key] = behaves[key]/len(files)

	return behaves


# starting point to merge all metadata, give it spot_1 level folder

def runRouteSummary(outputRoot, spot, mergedFileName):
	rootPath = outputRoot + spot+ '/'
	folderList = os.listdir(rootPath)
	outFileHandle = open(mergedFileName, 'a')
	header = getMetaHeader()
	outFileHandle.write(header + '\n')

	for child in folderList:
		if(os.path.isdir(rootPath + child )):
			oneMeta = getMetaData(rootPath +child)
			if(oneMeta != None):
				for s in range(1, len(oneMeta)):
					outFileHandle.write(oneMeta[s])
	outFileHandle.close()

	


def getMetaHeader():
	header = 'simid\,averagedotproduct,totaldistance,fractiondistbelowten,distancetomeancenterlastpoint'
	return header
	

def getMetaData(folder):
	fileList = getFiles(folder)
	data = None
	for i in range ( 0, len(fileList) ):
		if(fileList[i][fileList[i].rfind('/')+1:] == 'metadata.csv'):
			data = loadCsvStrings(fileList[i])

	return data

# given a list of dictionaries merge to key, n1, n2
def mergeDictionaries(manyDicts):
	masterDict = {}

	for i in range(0, len(manyDicts)):
		for j in manyDicts[i]:
			if j not in masterDict:
				masterDict[j] = []
			
			masterDict[j].append( manyDicts[i][j] )
	return masterDict



# runner()
runRouteSummary('C:/agentout/', 'SPOT_1', 'C:/agentout/SPOT_1/allmetadata.csv')

# g
