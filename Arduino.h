/**
 * This file is used to add code intelligence to CLion without having to add all kinds of different
 * arduino libraries in the project. Just like the Arduino IDE adds all kinds of stuff to to your
 * project in the background, this file does the same thing.
 *
 * This file is comprized of the most important arduino header files. Some things are probably missing.
 * If you need something that is not here, add a pull request or make a ticket in the CLuino github
 * project.
 *
 * This file should not be used to compile code. The main purpose is helping the ide.
 *
 * To use this file, include the file in your project in a folder named 'library'.
 * In your CMakeList.txt add a line: include_directories("library")
 * In all the source files that use arduino code include the file using: #include <Arduino.h>
 **/




/**
 * This file is distributed under the same license as the original source code:
 *            GNU GENERAL PUBLIC LICENSE  Version 2, June 1991
 *
 *         original source code url:      https://github.com/arduino/Arduino
 *         original license url:          https://github.com/arduino/Arduino/blob/master/license.txt
 **/


#pragma once

#include <inttypes.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <math.h>

#include <avr/pgmspace.h>
#include <avr/io.h>
#include <avr/interrupt.h>

void yield(void);
#define HIGH 0x1
#define LOW  0x0

#define INPUT 0x0
#define OUTPUT 0x1
#define INPUT_PULLUP 0x2

#define PI 3.1415926535897932384626433832795
#define HALF_PI 1.5707963267948966192313216916398
#define TWO_PI 6.283185307179586476925286766559
#define DEG_TO_RAD 0.017453292519943295769236907684886
#define RAD_TO_DEG 57.295779513082320876798154814105
#define EULER 2.718281828459045235360287471352

#define SERIAL  0x0
#define DISPLAY 0x1

#define LSBFIRST 0
#define MSBFIRST 1

#define CHANGE 1
#define FALLING 2
#define RISING 3


#define min(a,b) ((a)<(b)?(a):(b))
#define max(a,b) ((a)>(b)?(a):(b))
#define abs(x) ((x)>0?(x):-(x))
#define constrain(amt,low,high) ((amt)<(low)?(low):((amt)>(high)?(high):(amt)))
#define round(x)     ((x)>=0?(long)((x)+0.5):(long)((x)-0.5))
#define radians(deg) ((deg)*DEG_TO_RAD)
#define degrees(rad) ((rad)*RAD_TO_DEG)
#define sq(x) ((x)*(x))

#define interrupts() sei()
#define noInterrupts() cli()

#define clockCyclesPerMicrosecond() ( F_CPU / 1000000L )
#define clockCyclesToMicroseconds(a) ( (a) / clockCyclesPerMicrosecond() )
#define microsecondsToClockCycles(a) ( (a) * clockCyclesPerMicrosecond() )

#define lowByte(w) ((uint8_t) ((w) & 0xff))
#define highByte(w) ((uint8_t) ((w) >> 8))

#define bitRead(value, bit) (((value) >> (bit)) & 0x01)
#define bitSet(value, bit) ((value) |= (1UL << (bit)))
#define bitClear(value, bit) ((value) &= ~(1UL << (bit)))
#define bitWrite(value, bit, bitvalue) (bitvalue ? bitSet(value, bit) : bitClear(value, bit))


typedef unsigned int word;

#define bit(b) (1UL << (b))

typedef bool boolean;
typedef uint8_t byte;

void init(void);
void initVariant(void);

int atexit(void (*func)()) __attribute__((weak));

void pinMode(uint8_t, uint8_t);
void digitalWrite(uint8_t, uint8_t);
int digitalRead(uint8_t);
int analogRead(uint8_t);
void analogReference(uint8_t mode);
void analogWrite(uint8_t, int);

unsigned long millis(void);
unsigned long micros(void);
void delay(unsigned long);
void delayMicroseconds(unsigned int us);
unsigned long pulseIn(uint8_t pin, uint8_t state, unsigned long timeout);
unsigned long pulseInLong(uint8_t pin, uint8_t state, unsigned long timeout);

void shiftOut(uint8_t dataPin, uint8_t clockPin, uint8_t bitOrder, uint8_t val);
uint8_t shiftIn(uint8_t dataPin, uint8_t clockPin, uint8_t bitOrder);

void attachInterrupt(uint8_t, void (*)(void), int mode);
void detachInterrupt(uint8_t);

void setup(void);
void loop(void);

#define analogInPinToBit(P) (P)


unsigned long pulseIn(uint8_t pin, uint8_t state, unsigned long timeout = 1000000L);
unsigned long pulseInLong(uint8_t pin, uint8_t state, unsigned long timeout = 1000000L);

void tone(uint8_t _pin, unsigned int frequency, unsigned long duration = 0);
void noTone(uint8_t _pin);

// WMath prototypes
long random(long);
long random(long, long);
void randomSeed(unsigned long);
long map(long, long, long, long, long);

/////////////////////////////////////////// STRING //////////////////////////////

class __FlashStringHelper;

// An inherited class for holding the result of a concatenation.  These
// result objects are assumed to be writable by subsequent concatenations.
class StringSumHelper;

// The string class
class String
{
    // use a function pointer to allow for "if (s)" without the
    // complications of an operator bool(). for more information, see:
    // http://www.artima.com/cppsource/safebool.html
    typedef void (String::*StringIfHelperType)() const;
    void StringIfHelper() const {}

public:
    // constructors
    // creates a copy of the initial value.
    // if the initial value is null or invalid, or if memory allocation
    // fails, the string will be marked as invalid (i.e. "if (s)" will
    // be false).
    String(const char *cstr = "");
    String(const String &str);
    String(const __FlashStringHelper *str);
    String(String &&rval);
    String(StringSumHelper &&rval);
    explicit String(char c);
    explicit String(unsigned char, unsigned char base=10);
    explicit String(int, unsigned char base=10);
    explicit String(unsigned int, unsigned char base=10);
    explicit String(long, unsigned char base=10);
    explicit String(unsigned long, unsigned char base=10);
    explicit String(float, unsigned char decimalPlaces=2);
    explicit String(double, unsigned char decimalPlaces=2);
    ~String(void);

    // memory management
    // return true on success, false on failure (in which case, the string
    // is left unchanged).  reserve(0), if successful, will validate an
    // invalid string (i.e., "if (s)" will be true afterwards)
    unsigned char reserve(unsigned int size);
    inline unsigned int length(void) const {return len;}

    // creates a copy of the assigned value.  if the value is null or
    // invalid, or if the memory allocation fails, the string will be
    // marked as invalid ("if (s)" will be false).
    String & operator = (const String &rhs);
    String & operator = (const char *cstr);
    String & operator = (const __FlashStringHelper *str);
    String & operator = (String &&rval);
    String & operator = (StringSumHelper &&rval);

    // concatenate (works w/ built-in types)

    // returns true on success, false on failure (in which case, the string
    // is left unchanged).  if the argument is null or invalid, the
    // concatenation is considered unsucessful.
    unsigned char concat(const String &str);
    unsigned char concat(const char *cstr);
    unsigned char concat(char c);
    unsigned char concat(unsigned char c);
    unsigned char concat(int num);
    unsigned char concat(unsigned int num);
    unsigned char concat(long num);
    unsigned char concat(unsigned long num);
    unsigned char concat(float num);
    unsigned char concat(double num);
    unsigned char concat(const __FlashStringHelper * str);

    // if there's not enough memory for the concatenated value, the string
    // will be left unchanged (but this isn't signalled in any way)
    String & operator += (const String &rhs)	{concat(rhs); return (*this);}
    String & operator += (const char *cstr)		{concat(cstr); return (*this);}
    String & operator += (char c)			{concat(c); return (*this);}
    String & operator += (unsigned char num)		{concat(num); return (*this);}
    String & operator += (int num)			{concat(num); return (*this);}
    String & operator += (unsigned int num)		{concat(num); return (*this);}
    String & operator += (long num)			{concat(num); return (*this);}
    String & operator += (unsigned long num)	{concat(num); return (*this);}
    String & operator += (float num)		{concat(num); return (*this);}
    String & operator += (double num)		{concat(num); return (*this);}
    String & operator += (const __FlashStringHelper *str){concat(str); return (*this);}

    friend StringSumHelper & operator + (const StringSumHelper &lhs, const String &rhs);
    friend StringSumHelper & operator + (const StringSumHelper &lhs, const char *cstr);
    friend StringSumHelper & operator + (const StringSumHelper &lhs, char c);
    friend StringSumHelper & operator + (const StringSumHelper &lhs, unsigned char num);
    friend StringSumHelper & operator + (const StringSumHelper &lhs, int num);
    friend StringSumHelper & operator + (const StringSumHelper &lhs, unsigned int num);
    friend StringSumHelper & operator + (const StringSumHelper &lhs, long num);
    friend StringSumHelper & operator + (const StringSumHelper &lhs, unsigned long num);
    friend StringSumHelper & operator + (const StringSumHelper &lhs, float num);
    friend StringSumHelper & operator + (const StringSumHelper &lhs, double num);
    friend StringSumHelper & operator + (const StringSumHelper &lhs, const __FlashStringHelper *rhs);

    // comparison (only works w/ Strings and "strings")
    operator StringIfHelperType() const { return buffer ? &String::StringIfHelper : 0; }
    int compareTo(const String &s) const;
    unsigned char equals(const String &s) const;
    unsigned char equals(const char *cstr) const;
    unsigned char operator == (const String &rhs) const {return equals(rhs);}
    unsigned char operator == (const char *cstr) const {return equals(cstr);}
    unsigned char operator != (const String &rhs) const {return !equals(rhs);}
    unsigned char operator != (const char *cstr) const {return !equals(cstr);}
    unsigned char operator <  (const String &rhs) const;
    unsigned char operator >  (const String &rhs) const;
    unsigned char operator <= (const String &rhs) const;
    unsigned char operator >= (const String &rhs) const;
    unsigned char equalsIgnoreCase(const String &s) const;
    unsigned char startsWith( const String &prefix) const;
    unsigned char startsWith(const String &prefix, unsigned int offset) const;
    unsigned char endsWith(const String &suffix) const;

    // character acccess
    char charAt(unsigned int index) const;
    void setCharAt(unsigned int index, char c);
    char operator [] (unsigned int index) const;
    char& operator [] (unsigned int index);
    void getBytes(unsigned char *buf, unsigned int bufsize, unsigned int index=0) const;
    void toCharArray(char *buf, unsigned int bufsize, unsigned int index=0) const
    { getBytes((unsigned char *)buf, bufsize, index); }
    const char* c_str() const { return buffer; }
    char* begin() { return buffer; }
    char* end() { return buffer + length(); }
    const char* begin() const { return c_str(); }
    const char* end() const { return c_str() + length(); }

    // search
    int indexOf( char ch ) const;
    int indexOf( char ch, unsigned int fromIndex ) const;
    int indexOf( const String &str ) const;
    int indexOf( const String &str, unsigned int fromIndex ) const;
    int lastIndexOf( char ch ) const;
    int lastIndexOf( char ch, unsigned int fromIndex ) const;
    int lastIndexOf( const String &str ) const;
    int lastIndexOf( const String &str, unsigned int fromIndex ) const;
    String substring( unsigned int beginIndex ) const { return substring(beginIndex, len); };
    String substring( unsigned int beginIndex, unsigned int endIndex ) const;

    // modification
    void replace(char find, char replace);
    void replace(const String& find, const String& replace);
    void remove(unsigned int index);
    void remove(unsigned int index, unsigned int count);
    void toLowerCase(void);
    void toUpperCase(void);
    void trim(void);

    // parsing/conversion
    long toInt(void) const;
    float toFloat(void) const;
    double toDouble(void) const;

protected:
    char *buffer;	        // the actual char array
    unsigned int capacity;  // the array length minus one (for the '\0')
    unsigned int len;       // the String length (not counting the '\0')
protected:
    void init(void);
    void invalidate(void);
    unsigned char changeBuffer(unsigned int maxStrLen);
    unsigned char concat(const char *cstr, unsigned int length);

    // copy and move
    String & copy(const char *cstr, unsigned int length);
    String & copy(const __FlashStringHelper *pstr, unsigned int length);
    void move(String &rhs);
};

class StringSumHelper : public String
{
public:
    StringSumHelper(const String &s) : String(s) {}
    StringSumHelper(const char *p) : String(p) {}
    StringSumHelper(char c) : String(c) {}
    StringSumHelper(unsigned char num) : String(num) {}
    StringSumHelper(int num) : String(num) {}
    StringSumHelper(unsigned int num) : String(num) {}
    StringSumHelper(long num) : String(num) {}
    StringSumHelper(unsigned long num) : String(num) {}
    StringSumHelper(float num) : String(num) {}
    StringSumHelper(double num) : String(num) {}
};

/////////////////////////////////////////// PRINT ///////////////////////////////

#define DEC 10
#define HEX 16
#define OCT 8
#define BIN 2

class Print
{
private:
    int write_error;
    size_t printNumber(unsigned long, uint8_t);
    size_t printFloat(double, uint8_t);
protected:
    void setWriteError(int err = 1) { write_error = err; }
public:
    Print() : write_error(0) {}

    int getWriteError() { return write_error; }
    void clearWriteError() { setWriteError(0); }

    virtual size_t write(uint8_t) = 0;
    size_t write(const char *str) {
        if (str == NULL) return 0;
        return write((const uint8_t *)str, strlen(str));
    }
    virtual size_t write(const uint8_t *buffer, size_t size);
    size_t write(const char *buffer, size_t size) {
        return write((const uint8_t *)buffer, size);
    }

    // default to zero, meaning "a single write may block"
    // should be overriden by subclasses with buffering
    virtual int availableForWrite() { return 0; }

    size_t print(const __FlashStringHelper *);
    size_t print(const String &);
    size_t print(const char[]);
    size_t print(char);
    size_t print(unsigned char, int = DEC);
    size_t print(int, int = DEC);
    size_t print(unsigned int, int = DEC);
    size_t print(long, int = DEC);
    size_t print(unsigned long, int = DEC);
    size_t print(double, int = 2);
    size_t print(const Printable&);

    size_t println(const __FlashStringHelper *);
    size_t println(const String &s);
    size_t println(const char[]);
    size_t println(char);
    size_t println(unsigned char, int = DEC);
    size_t println(int, int = DEC);
    size_t println(unsigned int, int = DEC);
    size_t println(long, int = DEC);
    size_t println(unsigned long, int = DEC);
    size_t println(double, int = 2);
    size_t println(const Printable&);
    size_t println(void);

    virtual void flush() { /* Empty implementation for backward compatibility */ }
};

/////////////////////////////////////////// STREAM ///////////////////////////////

class Stream : public Print
{
protected:
    unsigned long _timeout;      // number of milliseconds to wait for the next char before aborting timed read
    unsigned long _startMillis;  // used for timeout measurement
    int timedRead();    // read stream with timeout
    int timedPeek();    // peek stream with timeout
    int peekNextDigit(LookaheadMode lookahead, bool detectDecimal); // returns the next numeric digit in the stream or -1 if timeout

public:
    virtual int available() = 0;
    virtual int read() = 0;
    virtual int peek() = 0;

    Stream() {_timeout=1000;}

// parsing methods

    void setTimeout(unsigned long timeout);  // sets maximum milliseconds to wait for stream data, default is 1 second
    unsigned long getTimeout(void) { return _timeout; }

    bool find(char *target);   // reads data from the stream until the target string is found
    bool find(uint8_t *target) { return find ((char *)target); }
    // returns true if target string is found, false if timed out (see setTimeout)

    bool find(char *target, size_t length);   // reads data from the stream until the target string of given length is found
    bool find(uint8_t *target, size_t length) { return find ((char *)target, length); }
    // returns true if target string is found, false if timed out

    bool find(char target) { return find (&target, 1); }

    bool findUntil(char *target, char *terminator);   // as find but search ends if the terminator string is found
    bool findUntil(uint8_t *target, char *terminator) { return findUntil((char *)target, terminator); }

    bool findUntil(char *target, size_t targetLen, char *terminate, size_t termLen);   // as above but search ends if the terminate string is found
    bool findUntil(uint8_t *target, size_t targetLen, char *terminate, size_t termLen) {return findUntil((char *)target, targetLen, terminate, termLen); }

    long parseInt(LookaheadMode lookahead = SKIP_ALL, char ignore = NO_IGNORE_CHAR);
    // returns the first valid (long) integer value from the current position.
    // lookahead determines how parseInt looks ahead in the stream.
    // See LookaheadMode enumeration at the top of the file.
    // Lookahead is terminated by the first character that is not a valid part of an integer.
    // Once parsing commences, 'ignore' will be skipped in the stream.

    float parseFloat(LookaheadMode lookahead = SKIP_ALL, char ignore = NO_IGNORE_CHAR);
    // float version of parseInt

    size_t readBytes( char *buffer, size_t length); // read chars from stream into buffer
    size_t readBytes( uint8_t *buffer, size_t length) { return readBytes((char *)buffer, length); }
    // terminates if length characters have been read or timeout (see setTimeout)
    // returns the number of characters placed in the buffer (0 means no valid data found)

    size_t readBytesUntil( char terminator, char *buffer, size_t length); // as readBytes with terminator character
    size_t readBytesUntil( char terminator, uint8_t *buffer, size_t length) { return readBytesUntil(terminator, (char *)buffer, length); }
    // terminates if length characters have been read, timeout, or if the terminator character  detected
    // returns the number of characters placed in the buffer (0 means no valid data found)

    // Arduino String functions to be added here
    String readString();
    String readStringUntil(char terminator);

protected:
    long parseInt(char ignore) { return parseInt(SKIP_ALL, ignore); }
    float parseFloat(char ignore) { return parseFloat(SKIP_ALL, ignore); }
    // These overload exists for compatibility with any class that has derived
    // Stream and used parseFloat/Int with a custom ignore character. To keep
    // the public API simple, these overload remains protected.

    struct MultiTarget {
        const char *str;  // string you're searching for
        size_t len;       // length of string you're searching for
        size_t index;     // index used by the search routine.
    };

    // This allows you to search for an arbitrary number of strings.
    // Returns index of the target that is found first or -1 if timeout occurs.
    int findMulti(struct MultiTarget *targets, int tCount);
};

/////////////////////////////////////////// SERIAL ///////////////////////////////

#define SERIAL_5N1 0x00
#define SERIAL_6N1 0x02
#define SERIAL_7N1 0x04
#define SERIAL_8N1 0x06
#define SERIAL_5N2 0x08
#define SERIAL_6N2 0x0A
#define SERIAL_7N2 0x0C
#define SERIAL_8N2 0x0E
#define SERIAL_5E1 0x20
#define SERIAL_6E1 0x22
#define SERIAL_7E1 0x24
#define SERIAL_8E1 0x26
#define SERIAL_5E2 0x28
#define SERIAL_6E2 0x2A
#define SERIAL_7E2 0x2C
#define SERIAL_8E2 0x2E
#define SERIAL_5O1 0x30
#define SERIAL_6O1 0x32
#define SERIAL_7O1 0x34
#define SERIAL_8O1 0x36
#define SERIAL_5O2 0x38
#define SERIAL_6O2 0x3A
#define SERIAL_7O2 0x3C
#define SERIAL_8O2 0x3E

class HardwareSerial : public Stream {
public:
    void begin(unsigned long baud) { begin(baud, SERIAL_8N1); }
    void begin(unsigned long, uint8_t);
    void end();
    virtual int available(void);
    virtual int peek(void);
    virtual int read(void);
    virtual int availableForWrite(void);
    virtual void flush(void);
    virtual size_t write(uint8_t);
    inline size_t write(unsigned long n) { return write((uint8_t)n); }
    inline size_t write(long n) { return write((uint8_t)n); }
    inline size_t write(unsigned int n) { return write((uint8_t)n); }
    inline size_t write(int n) { return write((uint8_t)n); }
    operator bool() { return true; }
};
HardwareSerial Serial;