/**
 */
package at.ac.tuwien.big.momot.examples.modularization.jsme.modularization.util;

import at.ac.tuwien.big.momot.examples.modularization.jsme.modularization.ModularizationModel;
import at.ac.tuwien.big.momot.examples.modularization.jsme.modularization.ModularizationPackage;
import at.ac.tuwien.big.momot.examples.modularization.jsme.modularization.Module;
import at.ac.tuwien.big.momot.examples.modularization.jsme.modularization.NamedElement;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * 
 * @see at.ac.tuwien.big.momot.examples.modularization.jsme.modularization.ModularizationPackage
 * @generated
 */
public class ModularizationAdapterFactory extends AdapterFactoryImpl {
   /**
    * The cached model package.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   protected static ModularizationPackage modelPackage;

   /**
    * The switch that delegates to the <code>createXXX</code> methods.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   protected ModularizationSwitch<Adapter> modelSwitch = new ModularizationSwitch<Adapter>() {
      @Override
      public Adapter caseClass(final at.ac.tuwien.big.momot.examples.modularization.jsme.modularization.Class object) {
         return createClassAdapter();
      }

      @Override
      public Adapter caseModularizationModel(final ModularizationModel object) {
         return createModularizationModelAdapter();
      }

      @Override
      public Adapter caseModule(final Module object) {
         return createModuleAdapter();
      }

      @Override
      public Adapter caseNamedElement(final NamedElement object) {
         return createNamedElementAdapter();
      }

      @Override
      public Adapter defaultCase(final EObject object) {
         return createEObjectAdapter();
      }
   };

   /**
    * Creates an instance of the adapter factory.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public ModularizationAdapterFactory() {
      if(modelPackage == null) {
         modelPackage = ModularizationPackage.eINSTANCE;
      }
   }

   /**
    * Creates an adapter for the <code>target</code>.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @param target
    *           the object to adapt.
    * @return the adapter for the <code>target</code>.
    * @generated
    */
   @Override
   public Adapter createAdapter(final Notifier target) {
      return modelSwitch.doSwitch((EObject) target);
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link at.ac.tuwien.big.momot.examples.modularization.jsme.modularization.Class <em>Class</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see at.ac.tuwien.big.momot.examples.modularization.jsme.modularization.Class
    * @generated
    */
   public Adapter createClassAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for the default case.
    * <!-- begin-user-doc -->
    * This default implementation returns null.
    * <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @generated
    */
   public Adapter createEObjectAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link at.ac.tuwien.big.momot.examples.modularization.jsme.modularization.ModularizationModel <em>Model</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see at.ac.tuwien.big.momot.examples.modularization.jsme.modularization.ModularizationModel
    * @generated
    */
   public Adapter createModularizationModelAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link at.ac.tuwien.big.momot.examples.modularization.jsme.modularization.Module <em>Module</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see at.ac.tuwien.big.momot.examples.modularization.jsme.modularization.Module
    * @generated
    */
   public Adapter createModuleAdapter() {
      return null;
   }

   /**
    * Creates a new adapter for an object of class
    * '{@link at.ac.tuwien.big.momot.examples.modularization.jsme.modularization.NamedElement <em>Named Element</em>}'.
    * <!-- begin-user-doc -->
    * This default implementation returns null so that we can easily ignore cases;
    * it's useful to ignore a case when inheritance will catch all the cases anyway.
    * <!-- end-user-doc -->
    * 
    * @return the new adapter.
    * @see at.ac.tuwien.big.momot.examples.modularization.jsme.modularization.NamedElement
    * @generated
    */
   public Adapter createNamedElementAdapter() {
      return null;
   }

   /**
    * Returns whether this factory is applicable for the type of the object.
    * <!-- begin-user-doc -->
    * This implementation returns <code>true</code> if the object is either the model's package or is an instance object
    * of the model.
    * <!-- end-user-doc -->
    * 
    * @return whether this factory is applicable for the type of the object.
    * @generated
    */
   @Override
   public boolean isFactoryForType(final Object object) {
      if(object == modelPackage) {
         return true;
      }
      if(object instanceof EObject) {
         return ((EObject) object).eClass().getEPackage() == modelPackage;
      }
      return false;
   }

} // ModularizationAdapterFactory
