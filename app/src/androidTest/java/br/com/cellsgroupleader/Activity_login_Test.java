package br.com.cellsgroupleader;


import android.view.*;

import androidx.test.espresso.*;
import androidx.test.filters.*;
import androidx.test.rule.*;
import androidx.test.runner.*;

import org.hamcrest.Description;
import org.hamcrest.*;
import org.junit.*;
import org.junit.runner.*;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

@LargeTest
@RunWith( AndroidJUnit4.class )
public class Activity_login_Test{
   
   @Rule
   public ActivityTestRule < Activity_splash_screen > mActivityTestRule = new ActivityTestRule <>( Activity_splash_screen.class );
   
   @Test
   public void activity_login_Test( ){
      ViewInteraction textInputEditText = onView(
         childAtPosition(
            childAtPosition(
               withId( R.id.email ) ,
               0 ) ,
            0 ) );
      textInputEditText.perform( scrollTo( ) , click( ) );
      
      ViewInteraction textInputEditText2 = onView(
         childAtPosition(
            childAtPosition(
               withId( R.id.email ) ,
               0 ) ,
            0 ) );
      textInputEditText2.perform( scrollTo( ) , replaceText( "joseb@gmail.com" ) , closeSoftKeyboard( ) );
      
      ViewInteraction textInputEditText3 = onView(
         allOf( withText( "joseb@gmail.com" ) ,
            childAtPosition(
               childAtPosition(
                  withId( R.id.email ) ,
                  0 ) ,
               0 ) ) );
       textInputEditText3.check(matches(withText(endsWith("joseb@gmail.com") )));
       textInputEditText3.perform( pressImeActionButton( ) );
      
      ViewInteraction textInputEditText4 = onView(
         childAtPosition(
            childAtPosition(
               withId( R.id.password ) ,
               0 ) ,
            0 ) );
      textInputEditText4.perform( scrollTo( ) , replaceText( "123456" ) , closeSoftKeyboard( ) );
      textInputEditText4.check(matches(withText(endsWith("123456")) ) );
      
      ViewInteraction appCompatButton = onView(
         allOf( withId( R.id.btnEnviarLogin ) , withText( "Entrar" ) ,
            childAtPosition(
               childAtPosition(
                  withClassName( is( "android.widget.LinearLayout" ) ) ,
                  4 ) ,
               1 ) ) );
      appCompatButton.perform( scrollTo( ) , click( ) );
   }
   
   private static Matcher < View > childAtPosition(
      final Matcher < View > parentMatcher , final int position ){
      
      return new TypeSafeMatcher < View >( ){
         @Override
         public void describeTo( Description description ){
            description.appendText( "Child at position " + position + " in parent " );
            parentMatcher.describeTo( description );
         }
         
         @Override
         public boolean matchesSafely( View view ){
            ViewParent parent = view.getParent( );
            return parent instanceof ViewGroup && parentMatcher.matches( parent )
               && view.equals( ( ( ViewGroup ) parent ).getChildAt( position ) );
         }
      };
   }
}
