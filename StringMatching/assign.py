import string
import re

f = open("AESOP TALES (with random URLs).txt")

#function to find all distinct characters in text file
def find_characters():
	s = set()
	for line in f:
		for word in line.split():
			s = s | set(word)
	s = list(s)
	s.append('\n')
	s.append(' ')
	s = sorted(s)
	return s
character = find_characters()

def Transition():
	d = dict()
	for c in character:
	    d[c] = [0]*10
	d['h'] = [1,1,1,1,1,1,1,1,1,9]
	d['f'] = [2,2,2,2,2,2,2,2,2,9]
	d['t'] = [0,3,4,4,0,0,0,0,0,9]
	d['p'] = [0,0,0,0,5,0,0,0,0,9]
	d['s'] = [0,0,0,0,0,7,0,0,0,9]
	d[':'] = [0,0,0,0,0,6,0,6,0,9]
	d['/'] = [0,0,0,0,0,0,8,0,9,9]
	for key in d.keys():
	    d[key][9] = 9
	return d

def find_urls():
	table = Transition()
	ftext = open("AESOP TALES (with random URLs).txt").read()
	count = 0
	urls = []
	s = ''
	for c in ftext:
		count = table[c][count]
		if count == 0:
			s = ''
		elif count == 9 and (c == '\n' or c == ' '):
			urls.append(s)
			count = 0
			s = ''
		else:
			s = s+c
	return urls

def rabin_karp(text, pattern):
	n = len(text)
	m = len(pattern)
	d = len(character)
	q = 101
	h = pow(d,m-1) % q
	p = 0
	t = 0
	ocurrences = 0
	indexes = []
	#preprocessing
	for i in range(m):
		p = (d*p + ord(pattern[i])) % q
		t = (d*t + ord(text[i])) % q

	#search
	for j in range(n-m+1):
		if p == t:
			if pattern == text[j:j+m]:
				#print("Pattern occurs with shift",j)
				indexes.append(j)
				ocurrences+=1
		if j < n-m:
			t = abs((d * (t - ord(text[j])*h) + ord(text[j+m])) % q)
	return ocurrences,indexes

def knuth_morris(text, pattern):
	n = len(text)
	m = len(pattern)
	pf = computePrefix(pattern)
	q = 0
	ocurrences = 0
	indexes = []
	for i in range(n):
		while q > 0 and pattern[q] != text[i]:
			q = pf[q]
		if pattern[q] == text[i]:
			q+=1
		if q == m:
			ocurrences+=1
			indexes.append(i-m+1)
			#print("Pattern occurs with shift",i-m+1)
			q = pf[q-1]
	return ocurrences,indexes

def computePrefix(P):
	m = len(P)
	pf = [0] * m
	k = 0
	for q in range(1,m):
		while k > 0 and P[k] != P[q]:
			k = pf[k]
		if P[k] == P[q]:
			k+=1
		pf[q] = k
	return pf

def build_cross_index(txtfile,algo):
	words = []
	punct = set(string.punctuation)
	f = open(txtfile)
	fstr = str()
	d = dict()
	for line in f:
		fstr = fstr + line.lower()
		for word in line.split():
			#remove any punctuations and digits from the word
			temp = ''.join(ch for ch in word if (ch not in punct) and (not ch.isdigit()))
			if temp != '' and len(temp)>1:
				#fstr = fstr.lower() + ' ' + temp.lower()
				words.append(temp.lower())
			
	words = sorted(set(words))
	#print(words)
	d = dict()
	for word in words[0]:
		word = 'lion'
		temp = algo(fstr,word)
		d[word] = [temp[0]] #d[word] = [occurrences]
		for i in temp[1]: #for i in indexes of the word
			titleindexes = sorted([(j,index[j]) for j in index if index[j]<=i],key = lambda x: x[1],reverse = True)
			if titleindexes!=[]:
				d[word].append(titleindexes[0][0])
			
	l = sorted(d)
	ordered = []
	for key in l:
	    ordered.append(key+" : "+str(set(d[key])))
	return ordered

def build_suffixArray(text):
	n = len(text)
	suffixArr = [0] * n
	for i in range(n):
		suffixArr[i] = i
	suffixArr = sorted(suffixArr,key = lambda x: text[x:])

	return suffixArr

def build_lcp(text,suffixArr):
	n = len(suffixArr)
	lcp = [0]
	for i in range(1,n):
		s1 = text[suffixArr[i-1]:]
		s2 = text[suffixArr[i]:]
		k = 0
		m = min(len(s1),len(s2))
		for j in range(m+1):
			if j<m and s1[j] == s2[j]:
				k+=1
			else:
				lcp.append(k)
				break
	return lcp


def suffixArray_matching(text, pattern):
	n = len(text)
	m = len(pattern)
	suffixArr = build_suffixArray(text)
	lcp_arr = build_lcp(text,suffixArr)
	#print(suffixArr, lcp_arr)
	l = 0
	r = n-1
	occurences  = 0
	indices = []
	while(l <= r):
		mid = (l+r)//2
		if pattern == text[suffixArr[mid]:][:m]:
			occurences += 1
			indices.append(suffixArr[mid])
			#print(mid,suffixArr[mid])
			for i in range(mid,0,-1):
				#print(i,lcp_arr[i])
				if(lcp_arr[i]>=m):
					occurences+=1
					indices.append(suffixArr[i-1])
				else:
					break
			
			for i in range(mid+1,n):
				#print(i,lcp_arr[i])
				if(lcp_arr[i]>=m):
					occurences+=1
					indices.append(suffixArr[i])
				else:
					break
				
			
			return occurences,indices
		
		elif pattern < text[suffixArr[mid]:][:m]:
			r = mid-1
		else:
			l = mid + 1

def get_titles():
	f = open('modified2.txt','r')
	title=[]
	repeated = []
	counter=0
	for line in f.readlines():
		read=line
		if(read == '\n'):
			counter += 1
		else:
			if(counter == 2):
				if(read.strip() in title):
					repeated.append(read.strip())
				else:
					title.append(read.strip())
			counter = 0				
	f.close()
	return title
	
def index_titles(text):
	titles = get_titles()
	index = {}

	for i in titles:
		index[i] = knuth_morris(text,i)[1][0]
	return index


def find_pattern(pattern, algo, *textrange):
	start = textrange[0]
	end = textrange[1]
	text = ftext
	if type(start) == int and type(end) == int:
		if end < start:
			start,end = end,start
		text = text[start:end]
		ocurrences = algo(text,pattern)[0]
	
	elif type(start) == str and type(end) == str:
		if start in index and end in index:
			start = index[start]
			end = index[end]
		else:
			start = knuth_morris(text,start)[1][0]
			end = knuth_morris(text,end)[1][0]
			if end < start:
				start,end = end,start
		if start == None and end == None:
			ocurrences = algo(text,pattern)[0]
		else:
			ocurrences = algo(text[start:end], pattern)[0]
	return ocurrences


def find_max_palindrome(size,*textrange):
	start = textrange[0]
	end = textrange[1]
	text = ftext
	if type(start) == int and type(end) == int:
		if end < start:
			start,end = end,start
		text = text[start:end]
	
	elif type(start) == str and type(end) == str:
		if start in index and end in index:
			start = index[start]
			end = index[end]
		else:
			start = knuth_morris(text,start)[1][0]
			end = knuth_morris(text,end)[1][0]
			if end < start:
				start,end = end,start
		text = text[start:end]

	text = text.replace('\n','')
	punct = string.punctuation
	palindrome = []
	for word in text.split():
		word = ''.join(ch for ch in word if (ch not in punct) and (not ch.isdigit()))
		if word!='' and len(word)>=size:
			word = word+'#'+word[::-1]
			suf = build_suffixArray(word)
			lcp = build_lcp(word,suf)
			a = max(lcp)
			b = lcp.index(a)
			k = word.find('#')
			if a>=size and ((suf[b]>k and suf[b-1]<k) or (suf[b]<k and suf[b-1]>k)):
				if word[suf[b]:][:a] not in palindrome:
					palindrome.append(word[suf[b]:][:a])
	return palindrome	


def remove_space():
	f = open('op.txt','r')
	f1 = open('modified2.txt','w')
	for line in f.readlines():
		reformed = line
		#reformed=re.sub(r'\w+:\/{2}[\d\w-]+(\.[\d\w-]+)*(?:(?:\/[^\s/]*))*', '', read)
		if(reformed!='\n' and reformed!=" \n"):
			reformed=re.sub("\s\s+" , " ", reformed)
		elif(reformed == " \n"):
			reformed=re.sub("\s" , "", reformed)
		f1.write(reformed.rstrip()+'\n')

def remove_url():
	ftex = open("AESOP TALES (with random URLs).txt").read()
	fwr = open('op','w')
	#fwr.write(ftex)
	urls = sorted(find_urls(),key = lambda x : len(x),reverse = True)
	#print(urls)
	for i in urls:
		if i in ftex:
			ftex = ftex.replace(i,'')
	
	fwr.write(ftex)
	remove_space()


#remove_url()
ftext = open('modified2.txt').read()
titles = get_titles()
index = index_titles(ftext)


#print(find_urls())
#print(find_max_palindrome(4,350,100000))

#print(build_cross_index('sam.txt',knuth_morris))

#print(find_pattern('Wolf',knuth_morris,53,853)) #textrange are indices
#print(find_pattern('Weasel',rabin_karp, 'The Wolf and the Lamb','The Ass and the Grasshopper')) #textrange are story titles
#print(find_pattern('Weasel',knuth_morris,'tyrant','hostility')) #textrange are distinct patterns
#print(find_pattern('Weasel',rabin_karp,'hostility','tyrant')) #textrange are distinct patterns but in reversed order
#print(build_suffixArray(open('sam.txt').read()))
#print(knuth_morris(ftext[53:853],'Wolf'))



