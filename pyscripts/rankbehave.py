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

def getCsvHeaderAsArray(filename):
	openFile = open(filename, 'r')
	someList = list(openFile)
	openFile.close()
	theData = []
	firstRow  = someList[0]
	lineParts = firstRow.split(',')	
	
	return lineParts

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



# creates a file with all runs for one stat, by agent
def mergeByStat(rootPath, column):
	
	folderList = os.listdir(rootPath)
	
	#outFileHandle = open(mergedFileName, 'a')
	outFileHandle = None

	masterData = []
	for child in folderList:
		if(os.path.isdir(rootPath + child )):
			
			oneMetaPath = getMetaDataPath(rootPath +child)

			if outFileHandle == None:	
				outname = rootPath + getCsvHeaderAsArray(oneMetaPath)[column]+'.csv'
				if os.path.isfile(outname):
					os.remove(outname)
				outFileHandle = open(outname,'a')

			

			data = loadCsv(oneMetaPath)

			outFileHandle.write(child+',')
			for i in range(0, len(data)):
				row = data[i]
				outFileHandle.write(row[column]+',')

			outFileHandle.write('\n')
	outFileHandle.close()

# starting point to merge all metadata, give it spot_1 level folder

def mergeAllMetaDataFiles(outputRoot, spot, mergedFileName):
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


def mergeMetaFilesInFolderSet(folders, outFileName):
	outFileHandle = open(outFileName, 'a')
	header = getMetaHeader()
	outFileHandle.write(header + '\n')

	for folder in folders:
		if(os.path.isdir(folder  )):
			oneMeta = getMetaData(folder)
			if(oneMeta != None):
				for s in range(1, len(oneMeta)):
					outFileHandle.write(oneMeta[s])

	outFileHandle.close()

def getFolderList(rootPath):
	folderList = os.listdir(rootPath)
	fullpathlist = []
	for f in folderList:
		if os.path.isdir(rootPath+f):
			fullpathlist.append(rootPath+f)

	fullpathlist.sort()
	return fullpathlist

#get two lists, run each pair through mergemetafiles
def mergeMetaResultsByAgent(spot1, spot2):
	lista = getFolderList(spot1)
	listb = getFolderList(spot2)

	if len(lista) != len(listb):
		print 'alert the lists are not same'
	else:
		for i in range(0, len(lista)):
			if listb[i][listb[i].rfind('/'):] != lista[i][lista[i].rfind('/'):]:  
				print 'ALERT shouldnt merge ' + lista[i] + ' , ' + listb[i]
			else:
				print 'printing ' + lista[i]
				mergeMetaFilesInFolderSet([ lista[i], listb[i] ], 'C:/agentout/mergedagentmetadata/' + listb[i][listb[i].rfind('/')+1:]+'_merged.csv')



def getMetaHeader():
	header = 'simid,averagedotproduct,totaldistance,fractiondistbelowten,distancetomeancenterlastpoint'
	return header
	

def getMetaData(folder):
	fileList = getFiles(folder)
	data = None
	for i in range ( 0, len(fileList) ):
		if(fileList[i][fileList[i].rfind('/')+1:] == 'metadata.csv'):
			data = loadCsvStrings(fileList[i])

	return data

def getMetaDataPath(parentFolder):
	fileList = getFiles(parentFolder)
	data = None
	for i in range ( 0, len(fileList) ):
		if(fileList[i][fileList[i].rfind('/')+1:] == 'metadata.csv'):
			return fileList[i]

	


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
#mergeAllMetaDataFiles('C:/agentout/', 'SPOT_1', 'C:/agentout/SPOT_1/allmetadata.csv')
# mergeMetaFilesInFolderSet([ 'C:/agentout/SPOT_1/ROUTE_SAMPLER/', 'C:/agentout/SPOT_2/ROUTE_SAMPLER/'], 'C:/agentout/MERGED_ROUTE_SAMPLER.csv')
#mergeMetaResultsByAgent('C:/agentout/SPOT_1/', 'C:/agentout/SPOT_2/')

mergeByStat('C:/agentout/SPOT_1/', 1)
mergeByStat('C:/agentout/SPOT_1/', 2)
mergeByStat('C:/agentout/SPOT_1/', 4)
mergeByStat('C:/agentout/SPOT_1/', 6)
mergeByStat('C:/agentout/SPOT_1/', 7)

mergeByStat('C:/agentout/SPOT_2/', 1)
mergeByStat('C:/agentout/SPOT_2/', 2)
mergeByStat('C:/agentout/SPOT_2/', 4)
mergeByStat('C:/agentout/SPOT_2/', 6)
mergeByStat('C:/agentout/SPOT_2/', 7)

# g
