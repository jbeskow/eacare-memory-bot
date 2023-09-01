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
            var speaker = 'Erik' // Erik
            var language = 'Swedish' //Swedish
//            var speed = Math.floor(Math.random() * 200) + 50; // default 100
            return `\\rst\\\\vce=speaker=${speaker}\\\\vce=language=${language}\\\\vce=gender=male\\\\rspd=${speed}\\${sentence}`
//            return sentence
        },
        say(sentence, speed) {
            fetch(`/say?text=${this.generateAcapelaSentence(sentence, speed=speed)}`);
        },
        changePage() {
            if (this.activeTimeLineItem == 3) {
                fetch('/change/objects');
//            } else if (this.activeTimeLineItem == 6) {
//                fetch('/change/instructions');
            } else if (this.activeTimeLineItem == 5) {
                fetch('/change/draw');
            } else {
                fetch('/change/nothing');
            }
        },
        navigateInMenuByClick: function(index) {
            this.activeTimeLineItem = index
            this.changePage()
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
            this.changePage()
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
                    {'key': "2", 'words': ["Jag heter Dr Andersson"]},
                    {'key': "3", 'words': ["Vi kommer att göra ett litet minnestest. Det här är inte ett officiellt minnestest men det är löst baserat på flera olika typer av kognitionstest som används vid minnesutredningar."]},
                    {'key': "4", 'words': ["Det finns inga rätt och fel. Om du har svårt att förstå mig kan du alltid be mig att upprepa vad jag sa"]},
                    {'key': "5", 'words': ["Låter det bra?"]},
                    {'key': "6", 'words': ["Bra! Låt oss börja."]}
                ]
            }, {
                title: 'Date/Place',
                type: 'list',
                items: [
                    {'key': "1", 'words': ["Vilket år är det?"], "after_gesture": "smile_big"},
                    {'key': "2", 'words': ["Vilken årstid är det?"]},
//                    {'key': "3", 'words': ["Vad är det för datum idag?"]},
//                    {'key': "4", 'words': ["Vilken veckodag är det?"]},
//                    {'key': "6", 'words': ["Vilket land är vi i?"]},
//                    {'key': "7", 'words': ["Vilken stad är vi i?"]},
//                    {'key': "8", 'words': ["Vilket sjukhus är vi vid?"]},
//                    {'key': "0", 'words': ["Vilken våning är vi på?"]},
                ]
            }, {
                title: 'Word repetition',
                type: 'list',
                items: [
                    {'key': "1", 'words': ["Nu kommer jag att säga tre ord, och jag skulle vilja att du upprepar dem efter mig när jag är färdig."]},
                    {'key': "2", 'words': ["Jag kommer även att fråga om dem senare, så lägg dem på minnet. Är du redo?"]},
                    {'key': "3", 'words': ["Bananer."]},
                    {'key': "4", 'words': ["Mynt."]},
                    {'key': "5", 'words': ["Klocka."]},
                    {'key': "6", 'words': ["Kan du upprepa alla orden nu?"]},
                    {'key': "7", 'words': ["Vill du att jag repeterar orden?"]},
                    {'key': "8", 'words': ["Tyvärr tror jag att du missupfattade mig"]},
                    {'key': "9", 'words': ["Gör det då"]},
                ]
            },
//             {
//                title: 'Spell backwards',
//                type: 'list',
//                items: [
//                    {'key': "1", 'words': ["Skulle du kunna stava ordet planet baklänges?"]},
//                    {'key': "2", 'words': ["Börja med sista bokstaven"]},
//                    {'key': "3", 'words': ["Du kan börja nu"]},
//                    {'key': "4", 'words': ["Nästan!"]},
//                    {'key': "5", 'words': ["Försök igen"]}
//                ]
//            },
            {
                title: 'Two objects',
                type: 'list',
                items: [
                    {'key': "1", 'words': ["Var god och titta på skärmen framför dig. Vad heter de två objekten framför dig?"]},
                    {'key': "2", 'words': ["Genom att använda ditt pekfinger, vänligen dra cykeln in i den högra översta lådan"]},
                    {'key': "3", 'words': ["Dra nu fisken in i den vänstra nedre lådan, tack"]}
                ]
            },
             {
                title: 'Repeat words again',
                type: 'list',
                items: [
                    {'key': "1", 'words': ["Minns du att jag sa att jag kommer att fråga om de tre orden igen? Nu är det dags att repetera dem igen."]},
                    {'key': "2", 'words': ["Gör det nu tack"]},
                ]
            },

//            {
//                title: 'Ifs, buts',
//                type: 'list',
//                items: [
//                    {'key': "1", 'words': ["Vänligen upprepa följande mening"]},
//                    {'key': "2", 'words': ["Inga, om, men, eller varför"]},
//                ]
//            },
//             {
//                title: 'Instructions',
//                type: 'list',
//                items: [
//                    {'key': "1", 'words': ["Var god och titta på skärmen igen."]},
//                    {'key': "2", 'words': ["Läs vad som är skrivet och följ instruktionen"]},
//                    {'key': "3", 'words': ["Okej, tack så mycket."]},
//                    {'key': "4", 'words': ["Du kan nu öppna ögonen igen"]},
//                ]
//            },
            {
                title: 'Clock',
                type: 'list',
                items: [
                    {'key': "1", 'words': ["Vänligen var god och titta på skärmen igen."]},
                    {'key': "2", 'words': ["Genom att använda ditt pekfinger rita en klocka samt sätt ut alla siffror på den."]},
                    {'key': "3", 'words': ["Rita in visarna så att klockan står på tio över elva"]},
                    {'key': "4", 'words': ["Om du vill försöka igen kan du klicka på knappen rensa uppe i vänstra hörnet"]}
                ]
            }
//            , {
//                title: 'Fold',
//                type: 'list',
//                items: [
//                    {'key': "1", 'words': ["Vänligen var god och ta upp pappret du skrev på tidigare med höger hand"]},
//                    {'key': "2", 'words': ["Vik det nu på mitten och lägg det på golvet"]},
//                    {'key': "3", 'words': ["Bra!"]}
//                ]
//            }
            , {
                title: 'End',
                type: 'list',
                items: [
                    {'key': "1", 'words': ["Nu är vi färdiga"]},
                    {'key': "2", 'words': ["Bra jobbat!"]},
                    {'key': "3", 'words': ["Tack för att du deltog idag"]},
                    {'key': "4", 'words': ["Ha en fortsatt bra dag"]},
                    {'key': "5", 'words': ["Hejdå"]},
                ]
            }
        ],
        encouragement: [
            {'key': "q", 'words': ["Ja"], "type": "face"},
            {'key': "w", 'words': ["Nej."]},
            {'key': "e", 'words': ["Tack."]},
            {'key': "r", 'words': ["Okej."]},
            {'key': "t", 'words': ["Jag förstår.", "Förstått"]},
            {'key': "y", 'words': ["Vad bra!", "Schysst.", "Bra gjort.", "Strålande.", "Bra."]},
            {'key': "u", 'words': ["Oroa dig inte.", "Inga problem"]},
            {'key': "i", 'words': ["Jag vet tyvärr inte", "Jag vet inte"]},
            {'key': "o", 'words': ["Det finns inga felaktiga svar"]},
            {'key': "p", 'words': ["Det här går jättebra."]},
            {'key': "[", 'words': ["Aha, jag förstår."]},
            {'key': "]", 'words': ["Jag hörde tyvärr inte vad du sa. Skulle du kunna upprepa dig?"]},
            {'key': "\\", 'words': ["Det får du fråga forskarna om!"]},
            {'key': "space", 'words': [""]}
        ],
        refocusing: [
            {'key': "a", 'words': ["Jag vet inte."]},
            {'key': "s", 'words': [ "Kan vi fortsätta med frågorna nu?", "Redo för nästa fråga?"]},
            {'key': "d", 'words': ["Jag sa"]},
            {'key': "f", 'words': ["och,"]},
            {'key': "g", 'words': ["Förlåt.", "Ursäkta."]},
            {'key': "h", 'words': ["Fortsätt tack.", "Varsågod fortsätt."]},
            {'key': "j", 'words': ["Vad sa du?", "Skulle du kunna upprepa vad du sa?"]},
            {'key': "k", 'words': ["Okej"]},
            {'key': "l", 'words': ["åh"]},
            {'key': ";", 'words': ["Låt oss fortsätta"]},
            {'key': "\'", 'words': ["Försök igen."]}
        ]
    },
    delimiters: ['[[',']]']
})
