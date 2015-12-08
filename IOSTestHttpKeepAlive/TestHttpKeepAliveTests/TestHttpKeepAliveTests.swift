//
//  TestHttpKeepAliveTests.swift
//  TestHttpKeepAliveTests
//
//  Created by Sylvain Laurent on 07.12.15.
//  Copyright © 2015 Sylvain Laurent. All rights reserved.
//

import XCTest
@testable import TestHttpKeepAlive

class TestHttpKeepAliveTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testExample() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        
        let url : NSURL! = NSURL(string: "http://localhost:8080/")
        
        let cfg = NSURLSessionConfiguration.ephemeralSessionConfiguration()
        cfg.URLCache = nil
        
        let session = NSURLSession(configuration: cfg)
        
        var nbCalls = 0
        let exp = expectationWithDescription("performed all requests")
        
        let request = NSMutableURLRequest(URL: url, cachePolicy:NSURLRequestCachePolicy.ReloadIgnoringLocalAndRemoteCacheData, timeoutInterval: 60)
        request.HTTPMethod = "POST"
        
        func responseHandler(data: NSData?, response: NSURLResponse?, error: NSError?) -> Void {
            nbCalls++
            print("done request \(nbCalls) : ", terminator: "")
            if let httpResponse = response as? NSHTTPURLResponse {
                print("\(httpResponse.statusCode) ", terminator: "")
                print(NSString(data: data!, encoding: NSISOLatin1StringEncoding)!)
                
                if(nbCalls < 10) {
                    //trigger next request
                    session.dataTaskWithRequest(request, completionHandler: responseHandler).resume()
                } else {
                    exp.fulfill()
                }
            } else {
                print("error ?" + error!.description)
            }
            
        }
        
        let task = session.dataTaskWithURL(url, completionHandler: responseHandler)
        task.resume()
        
        super.waitForExpectationsWithTimeout(10, handler: nil)
    }
    
    
    
    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measureBlock {
            // Put the code you want to measure the time of here.
        }
    }
    
}
