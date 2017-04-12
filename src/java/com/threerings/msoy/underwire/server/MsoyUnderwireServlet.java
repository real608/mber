//
// $Id$

package com.threerings.msoy.underwire.server;

import java.io.*;
import java.lang.Runtime;
import java.lang.Process;

import com.google.inject.Inject;

import com.samskivert.servlet.util.CookieUtil;

import com.threerings.underwire.server.UnderContext;
import com.threerings.underwire.web.client.AuthenticationException;
import com.threerings.underwire.web.client.UnderwireException;
import com.threerings.underwire.web.server.UnderwireServlet;
import com.threerings.user.OOOUser;
import com.threerings.web.gwt.ServiceException;

import com.threerings.msoy.server.MemberNodeActions;
import com.threerings.msoy.server.persist.MemberRecord;
import com.threerings.msoy.server.persist.MemberRepository;
import com.threerings.msoy.underwire.gwt.MsoyAccount.SocialStatus;
import com.threerings.msoy.underwire.gwt.SupportService;
import com.threerings.msoy.web.gwt.WebCreds;
import com.threerings.msoy.web.server.MemberHelper;

/**
 * An underwire servlet which uses a the msoy connection provider and user manager.
 */
public class MsoyUnderwireServlet extends UnderwireServlet
    implements SupportService
{
    // from SupportService
    public void setSocialStatus (int memberId, SocialStatus status)
        throws UnderwireException
    {
        MemberRecord caller = requireAuthedSupport();
        MemberRecord memberRec = _memberRepo.loadMember(memberId);
        boolean greeter = status == SocialStatus.GREETER;
        boolean troublemaker = status == SocialStatus.TROUBLEMAKER;
        boolean ipbanned = status == SocialStatus.IPBANNED;
        boolean greeterChanged = greeter != memberRec.isGreeter();
        if (greeterChanged || troublemaker != memberRec.isTroublemaker() || ipbanned != memberRec.isIPBanned()) {
            memberRec.setFlag(MemberRecord.Flag.GREETER, greeter);
            memberRec.setFlag(MemberRecord.Flag.TROUBLEMAKER, troublemaker);
            memberRec.setFlag(MemberRecord.Flag.IPBANNED, ipbanned);
            _memberRepo.storeFlags(memberRec);
            recordEvent(String.valueOf(caller.memberId), String.valueOf(memberId),
                        "Changed social status to " + status);
            try {  
                    if(status != SocialStatus.IPBANNED ) //if we are not IP Banned, remove our IP from blacklist
                    { 
                      String currentLine;
                      File addressFilePathsTxT = new File("//home//msoy//Desktop//blockip//blacklist.txt"); // change this to the file paths (Should be in desktop)!
                      File tempFileTxT = new File(addressFilePathsTxT.getAbsolutePath() + ".txt"); //We're going to make a new file that's like the other one.
                      File addressFilePathsIps = new File("//home//msoy//Desktop//blockip//blacklist.ips"); // change this to the file paths (Should be in desktop)!
                      File tempFileIps = new File(addressFilePathsIps.getAbsolutePath() + ".ips"); //We're going to make a new file that's like the other one.
                      
                      BufferedReader readIPAddress = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(addressFilePathsTxT))));
                      BufferedWriter tempWriteIPAddressTxT = new BufferedWriter(new FileWriter(tempFileTxT, true)); //write into the temp. txt file
                      BufferedWriter tempWriteIPAddressIps = new BufferedWriter(new FileWriter(tempFileIps, true)); //write into the temp. ips file
                      
                      while((currentLine = readIPAddress.readLine()) != null) { // iterate through every line in the notepad
                       if( currentLine.contains("PlayerID is " + memberRec.memberId + " ")) continue;
                       tempWriteIPAddressTxT.write(currentLine + System.getProperty("line.separator"));      
                       tempWriteIPAddressIps.write(currentLine.substring(currentLine.lastIndexOf(" ") + 1)  + System.getProperty("line.separator"));   
                       } // end while statement    
        
                           //Delete the original files
                          if (!addressFilePathsTxT.delete())
                            System.out.println("Could not delete file");
                            
                            if (!addressFilePathsIps.delete())
                            System.out.println("Could not delete file");
        
                            //Rename the new file to the filename the original files had.
                           if (!tempFileTxT.renameTo(addressFilePathsTxT))
                            System.out.println("Could not rename file");  
                            
                           if (!tempFileIps.renameTo(addressFilePathsIps))
                            System.out.println("Could not rename file");  
        
                        tempWriteIPAddressIps.close();
                        tempWriteIPAddressTxT.close();
                        readIPAddress.close();
                        
                     // do the ip unban command
                     String command = "sudo ./fwall";
                     Runtime runtime = Runtime.getRuntime();
                     try {
                     Process process = runtime.exec(command, null, new File("//home/msoy//Desktop//blockip"));
                     } catch (IOException e) {
                     e.printStackTrace();
                     }
        
                 }
              } catch (IOException e) {
               // we're not going to output anything, just leave it.
              }  

            if (greeterChanged) {
                // let the world servers know about the info change
                MemberNodeActions.tokensChanged(memberRec.memberId, memberRec.toTokenRing());
            }
        }
    }

    protected MemberRecord requireAuthedSupport ()
        throws UnderwireException
    {
        try {
            MemberRecord memberRecord = _memberHelper.requireAuthedUser(CookieUtil.getCookieValue(
                getThreadLocalRequest(), WebCreds.credsCookie()));
            if (memberRecord == null || !memberRecord.isSupport()) {
                throw new AuthenticationException("m.access_denied");
            }
            return memberRecord;

        } catch (ServiceException se) {
            throw new AuthenticationException("m.access_denied");
        }
    }

    @Override // from UnderwireServlet
    protected UnderContext createContext ()
    {
        return _underCtx;
    }

    @Override // from UnderwireServlet
    protected int getSiteId ()
    {
        return OOOUser.METASOY_SITE_ID;
    }

    // our dependencies
    @Inject protected MemberHelper _memberHelper;
    @Inject protected MemberRepository _memberRepo;
    @Inject protected MsoyUnderContext _underCtx;
}
