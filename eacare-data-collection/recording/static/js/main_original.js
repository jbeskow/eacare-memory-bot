const SPEEDS = [75, 100, 125, 150, 175]


Vue.use(window.ShortKey)
Vue.component('navigation', {
    template: '#navigation-template',
    props: ['items', 'activetimelineitem'],
    methods: {
        navigate: function(index) {
            this.$emit('navigated-in-menu-by-click', index)
        },
        switchTab: function(event) {
            this.$emit('navigated-in-menu-by-keyboard', event.srcKey)
        }
    },
    delimiters: ['[[',']]']
});


Vue.component('list-item', {
    template: '#list-item-template',
    props: ['words', 'active', 'hotkey', 'light', 'highlight'],
    delimiters: ['[[',']]'],
    methods: {
        clicked() {
            this.$emit('list-click')
        },
        shortkey(event) {
            this.$emit('shortkey')
        }
    }
});


var ListComponent = Vue.extend({
    props: ['title', 'items'],
    delimiters: ['[[',']]'],
    data: function() {
        return {
            highlightedItems: []
        }
    },
    methods: {
        highlightItem(index) {
            if (this.highlightedItems.indexOf(index) === -1) {
                this.highlightedItems.push(index)
                var self = this
                setTimeout(function() {
                    self.highlightedItems = self.highlightedItems.filter((item) => item !== index)
                }, 500)
            }
        },
        randomSentence(sentences) {
            return sentences[Math.floor(Math.random()*sentences.length)];
        },
        say(index) {
            this.highlightItem(index)
            let sentence = this.randomSentence(this.items[index].words)
            this.$emit('say', sentence, 100)
        }
    }
})


Vue.component('timeline-list', ListComponent.extend({
    template: '#timeline-list-template',
    data: function() {
        return {
            activeItem: 0
        }
    },
    watch: {
        items: function (newitems, olditems) {
            this.activeItem = 0
        }
    },
    methods: {
        pickUtterance(event, index) {
            this.activeItem = index
            this.say(index)
        },
        listAction: function(event) {
            switch(event.srcKey) {
                case 'enter':
                    this.say(this.activeItem)
                break;
                case 'up':
                    if (this.activeItem > 0) {
                        this.activeItem--;
                    }
                break;
                case 'down':
                    if (this.activeItem < this.items.length - 1) {
                        this.activeItem++;
                    }
                break;
            }
        }
    }
}));







function generateWords() {
    let resultArray = []
    let words = [
//        'Bibelbältet', 'Kejsarspett', 'glaskupa', 'gårdsbruk', 'fordringshavare', 'förhandlingsrätt',
//        'påskkort', 'avläsare', 'blåsmedel', 'gränspris', 'översiktskurs', 'krafs', 'skruvdragning',
//        'sorteringsalgoritm', 'bergkänguru', 'bältessträckare', 'klassmöte', 'sandpump',
//        'kylargaller', 'kvalitetssäkringsprogram', 'kolonistuga', 'cykel', 'som', 'stor',
//         'fällkrage', 'vantrevnad', 'högspänningsstolpe', 'kontrollmanometer',
//        'öppningsdag', 'mattelärare', 'knappnålsdyna', 'automobilindustri',
//        'auktionsprovision', 'gruvhiss', 'sömngångare'
    'general',
    'collar',
    'incompetent',
    'narrow',
    'contain',
    'deceive',
    'stretch',
    'pickle',
    'careful',
    'bubble',
    'steel',
    'dry',
    'skin',
    'boiling',
    'call',
    'division',
    'gleaming',
    'aloof',
    'truthful',
    'paltry',
    'reach',
    'explain',
    'scold',
    'tank',
    'big',
    'train',
    'industry',
    'momentous',
    'cent',
    'phobic',
    'parsimonious',
    'thrill',
    'strip',
    'stop',
    'count',
    'error',
    'bedroom',
    'telling',
    'trip',
    'steadfast',
    'juicy',
    'gun',
    'watery',
    'connection',
    'innate',
    'suit',
    'doubtful',
    'trouble',
    'gabby',
    'blink',
    ].sort( () => Math.random() - 0.5);


    for (var i=0,j=words.length; i<j; i+=5) {
        resultArray.push(words.slice(i,i+5).map((word) => {
            let speed = SPEEDS[Math.floor(Math.random() * SPEEDS.length)]
            return {'word': word, 'speed': speed, 'originalSpeed': speed}
        }));
    }
    return resultArray;
}

Vue.component('sentences', {
    props: ['speeds'],
    delimiters: ['[[',']]'],
    template: '#sentences-template',
})

Vue.component('words', {
    props: ['speeds'],
    delimiters: ['[[',']]'],
    template: '#words-template',
    data: function() {
        return {
            highlightedItems: [],
            currentChunk: 0,
            currentWord: 0,
            words: generateWords()
        }
    },
    methods: {
        highlightItem(index) {
            if (this.highlightedItems.indexOf(index) === -1) {
                this.highlightedItems.push(index)
                var self = this
                setTimeout(function() {
                    self.highlightedItems = self.highlightedItems.filter((item) => item !== index)
                }, 500)
            }
        },
        changeSpeed(change) {
            var currentSpeedIndex = SPEEDS.indexOf(this.words[this.currentChunk][this.currentWord]['speed'])
            var newSpeedIndex = currentSpeedIndex
            switch(change) {
                case 'increase':
                    if (currentSpeedIndex < SPEEDS.length-1) {
                        newSpeedIndex = currentSpeedIndex+1
                    }
                break;
                case 'decrease':
                    if (currentSpeedIndex > 0) {
                        newSpeedIndex = currentSpeedIndex-1
                    }
                break;
            }

            this.words[this.currentChunk][this.currentWord]['speed'] = SPEEDS[newSpeedIndex]

        },
        previousWord() {
            if(this.currentWord > 0) {
                this.currentWord--
            } else if (this.currentChunk > 0) {
                this.currentChunk--
                this.currentWord = this.words[this.currentChunk].length-1

            }
        },
        nextWord() {
            if(this.currentWord < this.words[this.currentChunk].length-1) {
                this.currentWord++
            } else if(this.currentChunk < this.words.length -1) {
                this.currentWord = 0
                this.currentChunk++
            }
        },
        say() {
            this.highlightItem(this.currentWord)
            var word = this.words[this.currentChunk][this.currentWord]
            this.$emit('say', word['word'], word['speed'])
        }
    }
});

Vue.component('speed', {
    props: ['currentspeed', 'speeds', 'active', 'originalspeed'],
    delimiters: ['[[',']]'],
    template: '#speed-template',
    methods: {
        decrease() {
            this.$emit('change-speed', 'decrease')
        },
        increase() {
            this.$emit('change-speed', 'increase')
        }
    }
});


Vue.component('utterance-list', ListComponent.extend({
  template: '#utterance-list-template',
}));


Vue.component('farmi-start-stop', {
    template: '#start_stop-template',
    methods: {
        startFarmi: () => fetch('/farmi/start'),
        stopFarmi: () => fetch('/farmi/stop'),
    }
});



new Vue({
    el: '#app',
    methods: {
        generateAcapelaSentence(sentence, speed=100, language='German', speaker='Erik') {
//            var speaker = 'Erik' // Erik
//            var language = 'Swedish' //Swedish
//            var speed = Math.floor(Math.random() * 200) + 50; // default 100
            return `\\rst\\\\vce=speaker=${speaker}\\\\vce=language=${language}\\\\vce=gender=male\\\\rspd=${speed}\\${sentence}`
        },
        say(sentence, speed) {
            fetch(`/say?text=${this.generateAcapelaSentence(sentence, speed=speed)}`);
        },
        navigateInMenuByClick: function(index) {
            this.activeTimeLineItem = index
        },
        navigateInMenuByKeyboard: function(direction) {
            switch(direction) {
                case 'left':
                    if (this.activeTimeLineItem > 0) this.activeTimeLineItem--
                break;
                case 'right':
                    if (this.activeTimeLineItem < this.timeline.length - 1) this.activeTimeLineItem++
                break;
            }
        },
    },
    data: {
        speeds: SPEEDS,
        activeTimeLineItem: 0,
        timeline: [
            {
                title: 'Intro',
                type: 'list',
                items: [
                    {'key': "1", 'words': ["Hej"]},
                    {'key': "2", 'words': ["Jag heter Förhatt"]},
                    {'key': "3", 'words': ["Vi kommer att göra ett litet minnestest"]},
                    {'key': "4", 'words': ["Det går till såhär, först kommer jag att ställa några frågor, och sen kommer jag att be dej att komma ihåg flera ord och meningar."]},
                    {'key': "5", 'words': ["Det finns inga rätt och fel. Om du har svårt att förstå mej kan du alltid be mej att upprepa vad jag sa"]},
                    {'key': "6", 'words': ["Låter det bra?"]},
                    {'key': "7", 'words': ["Bra! Låt oss börja."]}
                ]
            }, {
                title: 'Date/Place',
                type: 'list',
                items: [
                    {'key': "1", 'words': ["Vilket år är det?"]},
                    {'key': "2", 'words': ["Vilken årstid är det?"]},
                    {'key': "3", 'words': ["Vad är det för datum idag?"]},
                    {'key': "4", 'words': ["Vilken veckodag är det?"]},
                    {'key': "5", 'words': ["Vilken månad är det?"]},
                    {'key': "6", 'words': ["Vilket land är vi i?"]},
                    {'key': "7", 'words': ["Vilken stad är vi i?"]},
                    {'key': "8", 'words': ["Vilket universitet är vi vid?"]},
                    {'key': "9", 'words': ["Vad är detta för byggnad?"]},
                    {'key': "0", 'words': ["Vilken våning är vi på?"]}
                ]
            }, {
                title: 'Preparation for words',
                type: 'list',
                items: [
                    {'key': "1", 'words': ["Jag kommer att läsa upp fem ord. Jag skulle vilja att du repeterar dem efter mig."]},
                    {'key': "2", 'words': ["Är du redo?"]}
                ]
            }, {
                title: 'Words',
                type: 'words'
            }, {
                title: 'Preparation for sentences',
                type: 'list',
                items: [
                    {'key': "1", 'words': ["Jag kommer att läsa upp ett antal ord. Jag skulle vilja att du repeterar dem efter mig."]},
                    {'key': "2", 'words': ["Upprepa order direkt efter att jag sagt det."]},
                    {'key': "3", 'words': ["Är du redo?"]}
                ]
//            }, {
//                title: 'Sentences',
//                type: 'sentences',
//                items: [
//                    {'key': "1", 'words': ["Jag kommer att läsa upp fem ord. Jag skulle vilja att du repeterar dem efter mig."]}
//                ]
            }, {
                title: 'Preparation for arithmetics',
                type: 'list',
                items: [
                    {'key': "1", 'words': ["Nu kommer jag att be dig att minnas en serie med siffror."]},
                    {'key': "2", 'words': ["Är du redo?"]}
                ]
            }, {
                title: 'Arithmetics',
                type: 'list',
                items: [
                    {'key': "1", 'words': ["et"]},
                    {'key': "2", 'words': ["nio"]},
                    {'key': "3", 'words': ["fem"]},
                    {'key': "4", 'words': ["sju"]},
                    {'key': "5", 'words': ["vänligen repetera dem nu"]},
                ]
            }, {
                title: 'Arithmetics',
                type: 'list',
                items: [
                    {'key': "1", 'words': ["fem"]},
                    {'key': "2", 'words': ["sju"]},
                    {'key': "3", 'words': ["ett"]},
                    {'key': "4", 'words': ["ett"]},
                    {'key': "5", 'words': ["nio"]},
                    {'key': "6", 'words': ["sex"]},
                    {'key': "7", 'words': ["vänligen repetera dem nu"]},
                ]
            }, {
                title: 'Arithmetics',
                type: 'list',
                items: [
                    {'key': "1", 'words': ["nio"]},
                    {'key': "2", 'words': ["två"]},
                    {'key': "3", 'words': ["ett"]},
                    {'key': "4", 'words': ["sju"]},
                    {'key': "5", 'words': ["åtta"]},
                    {'key': "6", 'words': ["fyra"]},
                    {'key': "7", 'words': ["tre"]},
                    {'key': "8", 'words': ["ett"]},
                    {'key': "9", 'words': ["vänligen repetera dem nu"]},
                ]
            }, {
                title: 'Arithmetics',
                type: 'list',
                items: [
                    {'key': "1", 'words': ["nio"]},
                    {'key': "2", 'words': ["tre"]},
                    {'key': "3", 'words': ["sju"]},
                    {'key': "4", 'words': ["ett"]},
                    {'key': "5", 'words': ["två"]},
                    {'key': "6", 'words': ["ett"]},
                    {'key': "7", 'words': ["åtta"]},
                    {'key': "8", 'words': ["åtta"]},
                    {'key': "9", 'words': ["fyra"]},
                    {'key': "0", 'words': ["tre"]},
                    {'key': "-", 'words': ["vänligen repetera dem nu"]},
                ]
            }
        ],
        encouragement: [
            {'key': "q", 'words': ["Ja."]},
            {'key': "w", 'words': ["Nej."]},
            {'key': "e", 'words': ["Tack."]},
            {'key': "r", 'words': ["Jag förstår.", "Okej.", "Förstått"]},
            {'key': "t", 'words': ["Vad bra!", "Schysst.", "Bra gjort.", "Strålande."]},
            {'key': "y", 'words': ["Oroa dig inte.", "Inga problem"]},
            {'key': "u", 'words': ["Bra."]},
            {'key': "i", 'words': ["Det finns inga felaktiga svar"]},
            {'key': "o", 'words': ["Det här går jättebra."]},
            {'key': "p", 'words': ["Aha, jag förstår."]},
            {'key': "[", 'words': ["Så."]},
            {'key': "space", 'words': [""]}
        ],
        refocusing: [
            {'key': "a", 'words': ["Jag vet inte."]},
            {'key': "s", 'words': ["Låt oss fortsätta.", "Kan vi fortsätta med frågorna nu?", "Redo för nästa fråga?"]},
            {'key': "d", 'words': ["Jag sa"]},
            {'key': "f", 'words': ["och,"]},
            {'key': "g", 'words': ["Förlåt.", "Ursäkta."]},
            {'key': "h", 'words': ["Fortsätt tack.", "Varsågod, fortsätt."]},
            {'key': "j", 'words': ["Vad sa du?", "Skulle du kunna upprepa vad du sa?"]},
            {'key': "k", 'words': ["Okej"]},
            {'key': "l", 'words': ["åh."]}
        ]
    },
    delimiters: ['[[',']]']
})
